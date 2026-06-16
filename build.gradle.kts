// Root build.gradle.kts

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.dokka)
    alias(libs.plugins.hilt.gradle) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.spotless) apply false
}

val documentedProjects =
    setOf(
        "app",
        "core-data",
        "core-database",
        "core-domain",
        "core-rust",
        "core-testing",
        "core-ui",
        "feature-agent-navigation",
        "feature-agent",
        "test-app",
    )

dependencies {
    documentedProjects.forEach { moduleName -> dokka(project(":$moduleName")) }
}

tasks.register("apiDocs") {
    group = "documentation"
    description = "Generates aggregated Dokka API documentation for all documented modules."
    dependsOn(":dokkaGenerate")
}

tasks.register("checkApiDocs") {
    group = "verification"
    description = "Verifies that aggregated Dokka API documentation can be generated."
    dependsOn(":apiDocs")
}

subprojects {
    if (name in documentedProjects) {
        apply(plugin = "org.jetbrains.dokka")
    }

    configurations.configureEach {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.jetbrains.kotlin" && requested.name == "kotlin-metadata-jvm") {
                useVersion(libs.versions.kotlin.get())
                because("Hilt 2.59.2 brings older kotlin-metadata-jvm versions that cannot read Kotlin 2.4 metadata.")
            }
        }
    }

    afterEvaluate {
        if (plugins.hasPlugin("dev.detekt")) {
            configure<dev.detekt.gradle.extensions.DetektExtension> {
                buildUponDefaultConfig = true
                allRules = false
                config.setFrom(rootProject.file("detekt-config.yml"))
            }
        }
        if (plugins.hasPlugin("com.diffplug.spotless")) {
            configure<com.diffplug.gradle.spotless.SpotlessExtension> {
                kotlin {
                    target("src/**/*.kt")
                    targetExclude("**/build/**")
                    ktfmt("0.63").googleStyle()
                    trimTrailingWhitespace()
                    endWithNewline()
                }
                kotlinGradle {
                    target("*.kts")
                    ktfmt("0.63").googleStyle()
                    trimTrailingWhitespace()
                    endWithNewline()
                }
            }
        }
    }
}
