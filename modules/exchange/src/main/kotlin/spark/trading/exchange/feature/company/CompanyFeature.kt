package spark.trading.exchange.feature.company

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.ktor.controller.controller
import org.kodein.di.new
import org.kodein.di.singleton
import spark.trading.shared.feature.Feature

object CompanyFeature : Feature {
    private val MODULE_NAME = CompanyFeature::class.qualifiedName.toString()

    override val di: DI.Module = DI.Module(MODULE_NAME) {
        bind<CompanyController.Handler>() with singleton { new(::CompanyHandler) }
    }

    override fun Application.feature() {
        routing {
            controller("/companies") { new(::CompanyController) }
        }
    }
}
