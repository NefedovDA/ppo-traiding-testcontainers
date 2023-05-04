package spark.trading.admin.feature.user

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.ktor.controller.controller
import org.kodein.di.new
import org.kodein.di.singleton
import spark.trading.shared.feature.Feature

object UsersFeature : Feature {
    private val MODULE_NAME = UsersFeature::class.qualifiedName.toString()

    override val di: DI.Module = DI.Module(MODULE_NAME) {
        bind<UsersController.Handler>() with singleton { new(::UsersHandler) }
    }

    override fun Application.feature() {
        routing {
            controller("/users") { new(::UsersController) }
        }
    }
}
