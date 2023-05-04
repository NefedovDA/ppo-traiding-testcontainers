import spark.gradle.bundles

plugins {
    id("spark.core-conventions")
}

dependencies {
    implementation(bundles("ktor-client"))
}
