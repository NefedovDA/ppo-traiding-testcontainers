package spark.trading.admin.feature.user

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import spark.trading.admin.db.tables.ShareEntity
import spark.trading.admin.db.tables.UserEntity
import spark.trading.admin.view.ShareView
import spark.trading.admin.view.UserView
import spark.trading.exchange.client.ExchangeClient
import java.util.*

class UsersHandler(
    private val exchangeClient: ExchangeClient,
) : UsersController.Handler {
    override suspend fun getUsers(page: Int, size: Int): List<UserView> =
        transaction {
            UserEntity.all().limit(size, size * page.toLong())
                .map(UserEntity::view)
        }

    override suspend fun newUser(name: String, balance: Long): UserView =
        transaction {
            require(balance >= 0) { BALANCE_IS_NEGATIVE }
            val newUser =
                UserEntity.new(UUID.randomUUID()) {
                    this.name = name
                    this.balance = balance
                }
            newUser.view()
        }

    override suspend fun getUser(id: UUID): UserView =
        transaction { UserEntity.findById(id) }
            .let { requireNotNull(it) { USER_NOT_FOUND }.view() }

    override suspend fun deleteUser(id: UUID) =
        transaction {
            val user = UserEntity.findById(id)
            requireNotNull(user) { USER_NOT_FOUND }
            user.delete()
        }

    override suspend fun updateBalance(id: UUID, amount: Long): UserView =
        transaction {
            val user = UserEntity.findById(id)
            requireNotNull(user) { USER_NOT_FOUND }
            requireNotNull(user.balance + amount >= 0) { BALANCE_IS_NEGATIVE }
            user.balance = user.balance + amount
            user.view()
        }

    override suspend fun order(id: UUID, companyId: String, amount: Long): UserView =
        newSuspendedTransaction(Dispatchers.IO) {
            val user = UserEntity.findById(id)
            requireNotNull(user) { USER_NOT_FOUND }
            val userShare = user.shares.find { it.id.value == companyId }
            val company = exchangeClient.getCompany(companyId)

            if (amount == 0L) return@newSuspendedTransaction user.view()

            require(user.balance - company.share.price * amount >= 0) { NOT_ENOUGH_MONEY }
            require((userShare?.amount ?: 0) + amount >= 0) { NOT_ENOUGH_SHARES }

            exchangeClient.makeOrder(companyId, amount, company.share.price * amount)

            user.balance = user.balance - company.share.price * amount

            if (userShare != null) {
                userShare.amount = userShare.amount + amount
                if (userShare.amount == 0L) {
                    userShare.delete()
                }
            } else {
                ShareEntity.new(companyId) {
                    this.amount = amount
                    this.user = user
                }
            }

            user.view()
        }

    companion object {
        private const val USER_NOT_FOUND = "USER_NOT_FOUND"
        private const val BALANCE_IS_NEGATIVE = "BALANCE_IS_NEGATIVE"
        private const val NOT_ENOUGH_MONEY = "NOT_ENOUGH_MONEY"
        private const val NOT_ENOUGH_SHARES = "NOT_ENOUGH_SHARES"
    }
}

private fun UserEntity.view(): UserView =
    UserView(
        id = id.value,
        name = name,
        balance = balance,
        shares = transaction { shares.map(ShareEntity::view) },
    )

private fun ShareEntity.view(): ShareView =
    ShareView(
        companyId = id.value,
        amount = amount,
    )
