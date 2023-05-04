plugins {
    id("spark.core-conventions")
}

dependencies {
    implementation(libs.bundles.ktor.server)
    implementation(libs.bundles.ktor.client)

    implementation(libs.kodein.ktor.controller)
    implementation(libs.bundles.exposed)
}
