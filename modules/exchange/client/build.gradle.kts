@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("spark.client-conventions")
}

dependencies {
    implementation(projects.modules.shared)
    implementation(libs.ktor.server.core)
}
