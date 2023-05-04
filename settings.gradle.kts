rootProject.name = "trading"

includeBuild("infrastructure/conventions") { name = "conventions" }

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":modules:admin")
include(":modules:exchange")
include(":modules:exchange:client")
include(":modules:shared")

include(":integration-tests")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("infrastructure/libs.versions.toml"))
        }
    }
}
