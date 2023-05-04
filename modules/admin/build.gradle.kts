@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("spark.app-conventions")
}

application {
    mainClass.set("spark.trading.admin.AdminApp")
}

ktor {
    fatJar {
        archiveFileName.set("spark-trading-admin.jar")
    }
}

dependencies {
    implementation(projects.modules.shared)
    implementation(projects.modules.exchange.client)

    implementation(libs.config4k)
    implementation(libs.bundles.exposed)
}
