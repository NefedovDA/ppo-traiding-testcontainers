import io.gitlab.arturbosch.detekt.Detekt

plugins {
    id("io.gitlab.arturbosch.detekt")
}

detekt {
    toolVersion = "1.22.0"

    config = rootProject.files("detekt.yaml")
    buildUponDefaultConfig = true
}

tasks.withType<Detekt>().configureEach {
    reports {
        txt.required.set(true)
        xml.required.set(false)
        html.required.set(false)
    }
}
