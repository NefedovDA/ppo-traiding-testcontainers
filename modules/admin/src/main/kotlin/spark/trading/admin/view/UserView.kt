package spark.trading.admin.view

import kotlinx.serialization.Serializable
import spark.trading.shared.serialization.UUIDSerializer
import java.util.*

@Serializable
data class UserView(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val balance: Long,
    val shares: List<ShareView>,
)

@Serializable
data class ShareView(
    val companyId: String,
    val amount: Long,
)
