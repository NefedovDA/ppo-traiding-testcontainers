package spark.traiding

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.mockk.InternalPlatformDsl.toStr
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.wait.strategy.Wait

class IntegrationTestContext(
    val client: HttpClient,
    val adminHost: String,
    val exchangeHost: String,
) {
    companion object {
        private const val ADMIN_NETWORK_ALIAS = "admin"
        private const val EXCHANGE_NETWORK_ALIAS = "exchange"

        private const val ADMIN_IMAGE = "spark-trading-admin:latest"
        private const val EXCHANGE_IMAGE = "spark-trading-exchange:latest"

        private const val ADMIN_PORT = 8080
        private const val EXCHANGE_PORT = 3000

        private fun waitStrategy(port: Int) =
            Wait.forHttp("/healthcheck")
                .forPort(port)
                .forStatusCode(200)

        suspend operator fun invoke(test: suspend IntegrationTestContext.() -> Unit) {
            val network = Network.newNetwork()

            val exchangeContainer =
                GenericContainer(EXCHANGE_IMAGE)
                    .withNetwork(network)
                    .withNetworkAliases(EXCHANGE_NETWORK_ALIAS)
                    .withExposedPorts(EXCHANGE_PORT)
                    .withEnv(
                        mapOf(
                            "PORT" to EXCHANGE_PORT.toStr(),
                            "H2_FILE" to "/storage/db",
                        ),
                    )
                    .waitingFor(waitStrategy(EXCHANGE_PORT))

            val adminContainer =
                GenericContainer(ADMIN_IMAGE)
                    .withNetwork(network)
                    .withNetworkAliases(ADMIN_NETWORK_ALIAS)
                    .withExposedPorts(ADMIN_PORT)
                    .withEnv(
                        mapOf(
                            "PORT" to ADMIN_PORT.toStr(),
                            "H2_FILE" to "/storage/db",
                            "EXCHANGE_SERVER" to "http://$EXCHANGE_NETWORK_ALIAS:$EXCHANGE_PORT/",
                        ),
                    )
                    .dependsOn(exchangeContainer)
                    .waitingFor(waitStrategy(ADMIN_PORT))

            val client = HttpClient(OkHttp) {
                expectSuccess = false
                install(ContentNegotiation) { json() }

                defaultRequest {
                    contentType(ContentType.Application.Json)
                }
            }

            try {
                exchangeContainer.start()
                adminContainer.start()

                val exchangeHost =
                    "http://" + exchangeContainer.host + ":" + exchangeContainer.getMappedPort(EXCHANGE_PORT)
                val adminHost =
                    "http://" + adminContainer.host + ":" + adminContainer.getMappedPort(ADMIN_PORT)

                IntegrationTestContext(client, adminHost, exchangeHost).test()
            } finally {
                adminContainer.close()
                exchangeContainer.close()
                network.close()
            }
        }
    }
}
