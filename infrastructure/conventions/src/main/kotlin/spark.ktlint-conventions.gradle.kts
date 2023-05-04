import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    id("org.jlleitschuh.gradle.ktlint")
}

repositories {
    mavenCentral()
}

configure<KtlintExtension> {
    version.set("0.47.1")

    disabledRules.set(setOf("no-wildcard-imports"))
}
