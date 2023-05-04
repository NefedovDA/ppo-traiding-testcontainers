package spark.trading.exchange.client.view

import kotlinx.serialization.Serializable

@Serializable
data class ShareView(val price: Long, val amount: Long)

@Serializable
data class CompanyView(
    val id: String,
    val name: String,
    val share: ShareView,
)
