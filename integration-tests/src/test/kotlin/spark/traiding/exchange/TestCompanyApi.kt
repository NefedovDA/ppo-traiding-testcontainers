package spark.traiding.exchange

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import spark.traiding.IntegrationTestContext

class TestCompanyApi : BehaviorSpec() {
    init {
        Given("Trading system") {
            Then("Add single company - ok") {
                IntegrationTestContext {
                    val company =
                        CompanyView(
                            id = "YAN",
                            name = "Yandex",
                            share = ShareView(price = 100, amount = 1000),
                        )

                    client.post("$exchangeHost/companies") { setBody(company) }
                        .apply {
                            status shouldBe HttpStatusCode.OK
                            body<CompanyView>() shouldBe company
                        }
                    client.post("$exchangeHost/companies") { setBody(company) }
                        .apply {
                            status shouldNotBe HttpStatusCode.OK
                        }

                    client.get("$exchangeHost/companies/YAN")
                        .apply {
                            status shouldBe HttpStatusCode.OK
                            body<CompanyView>() shouldBe company
                        }
                }
            }

            Then("All companies - ok") {
                IntegrationTestContext {
                    client.get("$exchangeHost/companies") { paginated() }
                        .apply {
                            status shouldBe HttpStatusCode.OK
                            body<List<CompanyView>>() shouldHaveSize 0
                        }

                    val companies =
                        listOf(
                            CompanyView(
                                id = "YAN",
                                name = "Yandex",
                                share = ShareView(price = 100, amount = 1000),
                            ),
                            CompanyView(
                                id = "GGL",
                                name = "Google",
                                share = ShareView(price = 10, amount = 100),
                            ),
                            CompanyView(
                                id = "MLR",
                                name = "MailRu",
                                share = ShareView(price = 1, amount = 10),
                            ),
                        )

                    companies.forEach { company ->
                        client.post("$exchangeHost/companies") { setBody(company) }
                    }

                    client.get("$exchangeHost/companies") { paginated() }
                        .apply {
                            status shouldBe HttpStatusCode.OK
                            body<List<CompanyView>>() shouldContainExactlyInAnyOrder companies
                        }
                }
            }

            Then("Update share price - ok") {
                IntegrationTestContext {
                    val company =
                        CompanyView(
                            id = "YAN",
                            name = "Yandex",
                            share = ShareView(price = 100, amount = 1000),
                        )
                    client.post("$exchangeHost/companies") { setBody(company) }

                    client.put("$exchangeHost/companies/YAN/share/price?price=50")
                        .apply {
                            status shouldBe HttpStatusCode.OK
                            body<CompanyView>() shouldBe
                                company.copy(share = company.share.copy(price = 50))
                        }
                }
            }

            Then("Update share amount - ok") {
                IntegrationTestContext {
                    val company =
                        CompanyView(
                            id = "YAN",
                            name = "Yandex",
                            share = ShareView(price = 100, amount = 1000),
                        )
                    client.post("$exchangeHost/companies") { setBody(company) }

                    client.patch("$exchangeHost/companies/YAN/share/amount?amount=-100")
                        .apply {
                            status shouldBe HttpStatusCode.OK
                            body<CompanyView>() shouldBe
                                company.copy(share = company.share.copy(amount = 900))
                        }

                    client.patch("$exchangeHost/companies/YAN/share/amount?amount=100")
                        .apply {
                            status shouldBe HttpStatusCode.OK
                            body<CompanyView>() shouldBe company
                        }
                }
            }

            Then("Make order - ok") {
                IntegrationTestContext {
                    val company =
                        CompanyView(
                            id = "YAN",
                            name = "Yandex",
                            share = ShareView(price = 100, amount = 1000),
                        )
                    client.post("$exchangeHost/companies") { setBody(company) }

                    client.post("$exchangeHost/companies/YAN/order?amount=100&totalPrice=10000")
                        .apply {
                            status shouldBe HttpStatusCode.OK
                            body<CompanyView>() shouldBe
                                company.copy(share = company.share.copy(amount = 900))
                        }
                }
            }
        }
    }

    companion object {
        private fun HttpRequestBuilder.paginated(page: Int = 0, size: Int = 20) {
            url {
                parameters.append("page", page.toString())
                parameters.append("size", size.toString())
            }
        }
    }
}

@Serializable
data class ShareView(val price: Long, val amount: Long)

@Serializable
data class CompanyView(
    val id: String,
    val name: String,
    val share: ShareView,
)
