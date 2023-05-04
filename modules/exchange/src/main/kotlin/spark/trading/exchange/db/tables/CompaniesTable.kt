package spark.trading.exchange.db.tables

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable

object CompaniesTable : IdTable<String>() {
    override val id = text("id").entityId()

    val name = text("name")
    val sharePrice = long("share_price")
    val shareAmount = long("share_amount")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

class CompanyEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, CompanyEntity>(CompaniesTable)

    var name by CompaniesTable.name
    var sharePrice by CompaniesTable.sharePrice
    var shareAmount by CompaniesTable.shareAmount
}
