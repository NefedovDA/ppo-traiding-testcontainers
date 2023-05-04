@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("spark.app-conventions")
}

application {
    mainClass.set("spark.trading.exchange.ExchangeApp")
}

ktor {
    fatJar {
        archiveFileName.set("spark-trading-exchange.jar")
    }
}

dependencies {
    implementation(projects.modules.shared)
    implementation(libs.config4k)

    implementation(libs.bundles.exposed)
}
