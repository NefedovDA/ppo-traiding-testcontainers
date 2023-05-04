package spark.trading.exchange.client

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton
import spark.trading.shared.feature.Feature

class ExchangeClientFeature(servers: Map<String, String>) : Feature {
    override val di: DI.Module = DI.Module(MODULE_NAME) {
        bind<ExchangeClient>() with singleton {
            ExchangeClient(
                client = HttpClient(OkHttp) {
                    expectSuccess = true

                    install(ContentNegotiation) { json() }
                    install(Logging) {
                        logger = Logger.DEFAULT
                        level = LogLevel.ALL
                    }

                    defaultRequest {
                        url(servers.getValue(SERVER_NAME))
                    }
                },
            )
        }
    }

    companion object {
        private const val SERVER_NAME = "exchange"
        private val MODULE_NAME = ExchangeClientFeature::class.qualifiedName.toString()
    }
}
