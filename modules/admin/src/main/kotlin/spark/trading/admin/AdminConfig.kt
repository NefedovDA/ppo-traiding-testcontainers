package spark.trading.admin

import spark.trading.shared.configuration.AppConfig
import spark.trading.shared.db.DatabaseFeature

data class AdminConfig(
    val app: AppConfig,
    val storage: DatabaseFeature.Config,
    val servers: Map<String, String>,
)
