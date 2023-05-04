plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    maven {
        name = "m2"
        url = uri("https://plugins.gradle.org/m2/")
    }
}

dependencies {
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.kotlin.serialization.gradlePlugin)

    implementation(libs.ktor.gradlePlugin)

    implementation(libs.ktlint.gradlePlugin)
    implementation(libs.detekt.gradlePlugin)
}
