package spark.trading.shared.healthecheck

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton
import spark.trading.shared.feature.Feature

abstract class HealthcheckFeature : Feature {
    abstract suspend fun Application.healthcheck(): String?

    override val di: DI.Module = DI.Module(MODULE_NAME) {
        bind<HealthcheckClient>() with singleton {
            HealthcheckClient(
                client = HttpClient(OkHttp) {
                    expectSuccess = false

                    install(Logging) {
                        logger = Logger.DEFAULT
                        level = LogLevel.ALL
                    }
                },
            )
        }
    }

    override fun Application.feature() {
        routing {
            get("/healthcheck") {
                val error = this@feature.healthcheck()
                if (error == null) {
                    call.respondText(status = HttpStatusCode.OK, text = "OK")
                } else {
                    call.respondText(status = HttpStatusCode.NotAcceptable, text = error)
                }
            }
        }
    }

    companion object {
        private val MODULE_NAME = HealthcheckFeature::class.qualifiedName.toString()

        operator fun invoke(healthcheck: suspend Application.() -> String?): HealthcheckFeature =
            object : HealthcheckFeature() {
                override suspend fun Application.healthcheck(): String? = healthcheck()
            }
    }
}
