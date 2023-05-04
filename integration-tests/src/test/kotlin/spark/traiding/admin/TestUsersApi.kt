package spark.traiding.admin

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import spark.trading.shared.serialization.UUIDSerializer
import spark.traiding.IntegrationTestContext
import spark.traiding.exchange.CompanyView
import java.util.*

class TestUsersApi : BehaviorSpec() {
    init {
        Given("Trading system") {
            Then("Add new user - ok") {
                IntegrationTestContext {
                    val user: UserView
                    client.post("$adminHost/users?name=DIMA&balance=1000")
                        .apply {
                            status shouldBe HttpStatusCode.OK
                            user = body<UserView>()
                            user.name shouldBe "DIMA"
                            user.balance shouldBe 1000
                            user.shares shouldHaveSize 0
                        }

                    client.get("$adminHost/users/${user.id}")
                        .apply {
                            status shouldBe HttpStatusCode.OK
                            body<UserView>() shouldBe user
                        }
                }
            }

            Then("All users - ok") {
                IntegrationTestContext {
                    val users =
                        listOf("DIMA", "KIRILL", "BOB", "TOM", "JERRY")
                            .map { name ->
                                client.post("$adminHost/users?name=$name&balance=1000").body<UserView>()
                            }

                    client.get("$adminHost/users?page=0&size=20")
                        .apply {
                            status shouldBe HttpStatusCode.OK
                            body<List<UserView>>() shouldContainExactlyInAnyOrder users
                        }
                }
            }

            Then("Update balance - ok") {
                IntegrationTestContext {
                    val user = client.post("$adminHost/users?name=DIMA&balance=1000").body<UserView>()

                    client.patch("$adminHost/users/${user.id}/balance?amount=100")
                        .apply {
                            status shouldBe HttpStatusCode.OK
                            body<UserView>() shouldBe user.copy(balance = 1100)
                        }

                    client.patch("$adminHost/users/${user.id}/balance?amount=-200")
                        .apply {
                            status shouldBe HttpStatusCode.OK
                            body<UserView>() shouldBe user.copy(balance = 900)
                        }
                }
            }

            Then("Make order - ok") {
                IntegrationTestContext {
                    val company =
                        CompanyView(
                            id = "YAN",
                            name = "Yandex",
                            share = spark.traiding.exchange.ShareView(price = 100, amount = 1000),
                        )
                    client.post("$exchangeHost/companies") { setBody(company) }

                    val user = client.post("$adminHost/users?name=DIMA&balance=1000").body<UserView>()

                    client.post("$adminHost/users/${user.id}/order/${company.id}?amount=10")
                        .apply {
                            status shouldBe HttpStatusCode.OK
                            body<UserView>() shouldBe
                                user.copy(
                                    balance = 0,
                                    shares = listOf(ShareView(companyId = company.id, amount = 10)),
                                )
                        }
                    client.get("$exchangeHost/companies/${company.id}")
                        .apply {
                            status shouldBe HttpStatusCode.OK
                            body<CompanyView>() shouldBe
                                company.copy(share = company.share.copy(amount = 990))
                        }

                    client.post("$adminHost/users/${user.id}/order/${company.id}?amount=-10")
                        .apply {
                            status shouldBe HttpStatusCode.OK
                            body<UserView>() shouldBe user
                        }
                    client.get("$exchangeHost/companies/${company.id}")
                        .apply {
                            status shouldBe HttpStatusCode.OK
                            body<CompanyView>() shouldBe company
                        }
                }
            }
        }
    }
}

@Serializable
data class UserView(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val balance: Long,
    val shares: List<ShareView>,
)

@Serializable
data class ShareView(
    val companyId: String,
    val amount: Long,
)
