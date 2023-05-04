package spark.gradle

import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType

val Project.versionCatalog: VersionCatalog
    get() =
        extensions
            .getByType(VersionCatalogsExtension::class).named("libs")

fun Project.versions(alias: String): String =
    versionCatalog.findVersion(alias).get()
        .run {
            requiredVersion
                .ifEmpty { strictVersion.ifEmpty { preferredVersion } }
        }

fun Project.libs(alias: String): Provider<MinimalExternalModuleDependency> =
    versionCatalog.findLibrary(alias).get()

fun Project.bundles(alias: String): Provider<ExternalModuleDependencyBundle> =
    versionCatalog.findBundle(alias).get()
