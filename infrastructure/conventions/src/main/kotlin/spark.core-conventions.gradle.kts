import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import spark.gradle.bundles
import spark.gradle.libs
import spark.gradle.versions

plugins {
    id("spark.ktlint-conventions")
    id("spark.detekt-conventions")

    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
}

repositories {
    mavenCentral()
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = versions("java")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.jar.configure {
    archiveBaseName.set(makeJarName(project))
}

dependencies {
    implementation(libs("kodein-di"))
    implementation(libs("logback-classic"))

    testImplementation(bundles("kotest"))
}

fun makeJarName(project: Project): String {
    val parentJarName =
        project.parent
            ?.let { parent -> makeJarName(parent) + "-" }
            ?: ""
    return "$parentJarName${project.name}"
}
