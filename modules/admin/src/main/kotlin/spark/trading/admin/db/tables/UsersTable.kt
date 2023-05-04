package spark.trading.admin.db.tables

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import java.util.*

object UsersTable : IdTable<UUID>() {
    override val id = uuid("id").entityId()
    val name = text("name")
    val balance = long("balance")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

class UserEntity(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, UserEntity>(UsersTable)

    var name by UsersTable.name
    var balance by UsersTable.balance

    val shares by ShareEntity referrersOn SharesTable.userId
}
