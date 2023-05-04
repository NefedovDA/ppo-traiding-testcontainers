package spark.trading.exchange

import spark.trading.shared.configuration.AppConfig
import spark.trading.shared.db.DatabaseFeature

data class ExchangeConfig(
    val app: AppConfig,
    val storage: DatabaseFeature.Config,
)
