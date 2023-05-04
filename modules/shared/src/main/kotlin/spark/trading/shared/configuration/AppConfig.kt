package spark.trading.shared.configuration

data class AppConfig(
    val host: String = "0.0.0.0",
    val port: Int = 8080,
    val gracePeriodMillis: Long = 3000,
    val timeoutMillis: Long = 5000,
)
