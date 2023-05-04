package spark.trading.admin.feature.user

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.kodein.di.ktor.controller.AbstractDIController
import spark.trading.admin.view.UserView
import java.util.*

class UsersController(
    application: Application,
    private val handler: Handler,
) : AbstractDIController(application) {
    interface Handler {
        suspend fun getUsers(page: Int, size: Int): List<UserView>

        suspend fun newUser(name: String, balance: Long): UserView
        suspend fun getUser(id: UUID): UserView
        suspend fun deleteUser(id: UUID)

        suspend fun updateBalance(id: UUID, amount: Long): UserView

        suspend fun order(id: UUID, companyId: String, amount: Long): UserView
    }

    override fun Route.getRoutes() {
        get {
            val page: Int by call.request.queryParameters
            val size: Int by call.request.queryParameters

            call.respond(message = handler.getUsers(page, size))
        }

        post {
            val name: String by call.request.queryParameters
            val balance: Long by call.request.queryParameters

            call.respond(message = handler.newUser(name, balance))
        }

        route("/{id}") {
            get {
                val id: UUID by call.parameters

                call.respond(message = handler.getUser(id))
            }

            delete {
                val id: UUID by call.parameters
                handler.deleteUser(id)

                call.respondText(OK)
            }

            patch("/balance") {
                val id: UUID by call.parameters
                val amount: Long by call.request.queryParameters

                call.respond(message = handler.updateBalance(id, amount))
            }

            post("/order/{companyId}") {
                val id: UUID by call.parameters
                val companyId: String by call.parameters
                val amount: Long by call.request.queryParameters

                call.respond(message = handler.order(id, companyId, amount))
            }
        }
    }

    companion object {
        private const val OK = "OK"
    }
}
