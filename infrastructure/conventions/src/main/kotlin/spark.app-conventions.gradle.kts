import spark.gradle.bundles
import spark.gradle.libs

plugins {
    id("spark.core-conventions")

    id("io.ktor.plugin")
}

application {
    val isDevelopment = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(bundles("ktor-server"))
    implementation(libs("kodein-ktor-controller"))
}
