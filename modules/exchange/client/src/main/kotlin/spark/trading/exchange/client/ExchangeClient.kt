package spark.trading.exchange.client

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import spark.trading.exchange.client.view.CompanyView

class ExchangeClient(private val client: HttpClient) {
    suspend fun getCompany(id: String): CompanyView =
        client.get("companies/$id")
            .body()

    suspend fun makeOrder(id: String, amount: Long, totalPrice: Long): CompanyView =
        client.post("companies/$id/order?amount=$amount&totalPrice=$totalPrice")
            .body()
}
