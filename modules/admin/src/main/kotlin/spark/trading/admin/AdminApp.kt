package spark.trading.admin

import com.typesafe.config.ConfigFactory
import io.github.config4k.extract
import org.jetbrains.exposed.sql.SchemaUtils
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import spark.trading.admin.db.tables.SharesTable
import spark.trading.admin.db.tables.UsersTable
import spark.trading.admin.feature.user.UsersFeature
import spark.trading.exchange.client.ExchangeClientFeature
import spark.trading.shared.configuration.BaseApp
import spark.trading.shared.db.DatabaseFeature
import spark.trading.shared.feature.Feature
import spark.trading.shared.healthecheck.HealthcheckClient
import spark.trading.shared.healthecheck.HealthcheckFeature

class AdminApp(config: AdminConfig) : BaseApp(config.app) {
    override val features: List<Feature> =
        listOf(
            UsersFeature,
            ExchangeClientFeature(config.servers),
            DatabaseFeature(config.storage) {
                SchemaUtils.create(UsersTable)
                SchemaUtils.create(SharesTable)
            },
            HealthcheckFeature {
                val healthcheckClient by closestDI().instance<HealthcheckClient>()
                healthcheckClient.check(config.servers)
            },
        )

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val config =
                ConfigFactory.parseResources("admin.conf")
                    .resolve()
                    .extract<AdminConfig>("admin")

            AdminApp(config).run()
        }
    }
}
