package spark.trading.shared.configuration

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import org.kodein.di.DI
import org.kodein.di.ktor.di
import spark.trading.shared.feature.Feature

abstract class BaseApp(private val config: AppConfig) {
    open val features: List<Feature> get() = emptyList()

    open fun DI.MainBuilder.configureDI() {}
    open fun Application.configureKtor() {}

    fun run() {
        val engine = embeddedServer(
            factory = Netty,
            host = config.host,
            port = config.port,
            module = {
                di {
                    configureDI()
                    importAll(features.mapNotNull(Feature::di))
                }
                install(ContentNegotiation) { json() }
                configureKtor()
                features.forEach { it.run { feature() } }
            },
        )
        engine.addShutdownHook {
            engine.stop(
                gracePeriodMillis = config.gracePeriodMillis,
                timeoutMillis = config.timeoutMillis,
            )
        }
        engine.start(wait = true)
    }
}
