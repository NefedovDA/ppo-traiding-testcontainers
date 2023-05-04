package spark.trading.exchange

import com.typesafe.config.ConfigFactory
import io.github.config4k.extract
import org.jetbrains.exposed.sql.SchemaUtils
import spark.trading.exchange.db.tables.CompaniesTable
import spark.trading.exchange.feature.company.CompanyFeature
import spark.trading.shared.configuration.BaseApp
import spark.trading.shared.db.DatabaseFeature
import spark.trading.shared.feature.Feature
import spark.trading.shared.healthecheck.HealthcheckFeature

class ExchangeApp(config: ExchangeConfig) : BaseApp(config.app) {
    override val features: List<Feature> = listOf(
        CompanyFeature,
        DatabaseFeature(config.storage) {
            SchemaUtils.create(CompaniesTable)
        },
        HealthcheckFeature { null },
    )

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val config =
                ConfigFactory.parseResources("exchange.conf")
                    .resolve()
                    .extract<ExchangeConfig>("exchange")

            ExchangeApp(config).run()
        }
    }
}
