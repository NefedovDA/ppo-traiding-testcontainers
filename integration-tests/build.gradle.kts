@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("spark.core-conventions")
}

dependencies {
    implementation(projects.modules.shared)
    testImplementation(libs.bundles.ktor.client)
    testImplementation(libs.kotest.testcontainers)
}
