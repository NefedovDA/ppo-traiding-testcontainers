package spark.trading.shared.db

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction
import spark.trading.shared.feature.Feature

abstract class DatabaseFeature(private val config: Config) : Feature {
    data class Config(
        val file: String,
    )

    protected abstract fun Transaction.tables()

    override fun Application.feature() {
        val driverClassName = "org.h2.Driver"
        val jdbcURL = "jdbc:h2:file:${config.file}"
        val database = Database.connect(jdbcURL, driverClassName)
        transaction(database) { tables() }
    }

    companion object {
        operator fun invoke(config: Config, tables: Transaction.() -> Unit): DatabaseFeature =
            object : DatabaseFeature(config) {
                override fun Transaction.tables() = tables()
            }
    }
}
