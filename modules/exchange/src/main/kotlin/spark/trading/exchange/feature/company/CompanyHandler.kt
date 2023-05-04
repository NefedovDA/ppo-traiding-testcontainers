package spark.trading.exchange.feature.company

import org.jetbrains.exposed.sql.transactions.transaction
import spark.trading.exchange.db.tables.CompanyEntity
import spark.trading.exchange.view.CompanyView
import spark.trading.exchange.view.ShareView

class CompanyHandler : CompanyController.Handler {
    override suspend fun getCompanies(page: Int, size: Int): List<CompanyView> =
        transaction {
            CompanyEntity.all().limit(size, size * page.toLong()).map(CompanyEntity::view)
        }

    override suspend fun newCompany(company: CompanyView): CompanyView =
        transaction {
            require(company.share.price >= 0) { PRICE_IS_NEGATIVE }
            require(company.share.amount >= 0) { AMOUNT_IS_NEGATIVE }

            val newCompany = CompanyEntity.new(company.id) {
                name = company.name
                sharePrice = company.share.price
                shareAmount = company.share.amount
            }
            newCompany.view()
        }

    override suspend fun getCompany(id: String): CompanyView =
        transaction { CompanyEntity.findById(id) }
            .let { requireNotNull(it) { COMPANY_NOT_FOUND }.view() }

    override suspend fun deleteCompany(id: String): Unit =
        transaction {
            val company = CompanyEntity.findById(id)
            requireNotNull(company) { COMPANY_NOT_FOUND }
            company.delete()
        }

    override suspend fun setSharePrice(id: String, price: Long): CompanyView =
        transaction {
            val company = CompanyEntity.findById(id)
            requireNotNull(company) { COMPANY_NOT_FOUND }
            require(price >= 0) { PRICE_IS_NEGATIVE }

            company.sharePrice = price
            company.view()
        }

    override suspend fun changeShareAmount(id: String, amount: Long): CompanyView =
        transaction {
            val company = CompanyEntity.findById(id)
            requireNotNull(company) { COMPANY_NOT_FOUND }
            require(company.shareAmount + amount >= 0) { AMOUNT_IS_NEGATIVE }

            company.shareAmount = company.shareAmount + amount
            company.view()
        }

    override suspend fun makeOrder(id: String, amount: Long, totalPrice: Long): CompanyView =
        transaction {
            val company = CompanyEntity.findById(id)
            requireNotNull(company) { COMPANY_NOT_FOUND }
            require(company.shareAmount - amount >= 0) { NO_SUCH_AMOUNT }
            require(amount * company.sharePrice == totalPrice) { PRICE_IS_CHANGED }

            company.shareAmount = company.shareAmount - amount
            company.view()
        }

    companion object {
        private const val COMPANY_NOT_FOUND = "COMPANY_NOT_FOUND"
        private const val NO_SUCH_AMOUNT = "NO_SUCH_AMOUNT"
        private const val PRICE_IS_CHANGED = "PRICE_IS_CHANGED"
        private const val PRICE_IS_NEGATIVE = "PRICE_IS_NEGATIVE"
        private const val AMOUNT_IS_NEGATIVE = "AMOUNT_IS_NEGATIVE"
    }
}

private fun CompanyEntity.view(): CompanyView =
    CompanyView(
        id = id.value,
        name = name,
        share = ShareView(price = sharePrice, amount = shareAmount),
    )
