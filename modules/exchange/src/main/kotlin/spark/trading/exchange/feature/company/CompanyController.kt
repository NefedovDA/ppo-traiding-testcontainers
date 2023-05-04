package spark.trading.exchange.feature.company

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.kodein.di.ktor.controller.AbstractDIController
import spark.trading.exchange.view.CompanyView

class CompanyController(
    application: Application,
    private val handler: Handler,
) : AbstractDIController(application) {
    interface Handler {
        suspend fun getCompanies(page: Int, size: Int): List<CompanyView>

        suspend fun newCompany(company: CompanyView): CompanyView
        suspend fun getCompany(id: String): CompanyView
        suspend fun deleteCompany(id: String)

        suspend fun setSharePrice(id: String, price: Long): CompanyView
        suspend fun changeShareAmount(id: String, amount: Long): CompanyView

        suspend fun makeOrder(id: String, amount: Long, totalPrice: Long): CompanyView
    }

    override fun Route.getRoutes() {
        get {
            val page: Int by call.request.queryParameters
            val size: Int by call.request.queryParameters

            call.respond(message = handler.getCompanies(page, size))
        }

        post {
            val company = call.receive<CompanyView>()

            call.respond(message = handler.newCompany(company))
        }

        route("/{id}") {
            get {
                val id: String by call.parameters

                call.respond(message = handler.getCompany(id))
            }

            delete {
                val id: String by call.parameters
                handler.deleteCompany(id)
                call.respondText(OK)
            }

            post("/order") {
                val id: String by call.parameters
                val amount: Long by call.request.queryParameters
                val totalPrice: Long by call.request.queryParameters

                call.respond(message = handler.makeOrder(id, amount, totalPrice))
            }

            route("/share") {
                put("/price") {
                    val id: String by call.parameters
                    val price: Long by call.request.queryParameters

                    call.respond(message = handler.setSharePrice(id, price))
                }

                patch("/amount") {
                    val id: String by call.parameters
                    val amount: Long by call.request.queryParameters

                    call.respond(message = handler.changeShareAmount(id, amount))
                }
            }
        }
    }

    companion object {
        private const val OK = "OK"
    }
}
