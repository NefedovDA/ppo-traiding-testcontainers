package spark.trading.admin.db.tables

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable

object SharesTable : IdTable<String>() {
    override val id = text("company_id").entityId()
    val userId = reference("user_id", UsersTable.id)
    val amount = long("amount")

    override val primaryKey: PrimaryKey = PrimaryKey(id, userId)
}

class ShareEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, ShareEntity>(SharesTable)

    var amount by SharesTable.amount
    var user by UserEntity referencedOn SharesTable.userId
}
