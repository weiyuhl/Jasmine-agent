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
    alias(libs.plugins.screenshot) apply false
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

val kotlinVersion = libs.versions.kotlin.get()

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

    apply(plugin = "jacoco")

    pluginManager.withPlugin("com.android.application") {
        configure<com.android.build.api.dsl.ApplicationExtension> {
            buildTypes {
                getByName("debug") {
                    enableUnitTestCoverage = true
                    enableAndroidTestCoverage = true
                }
            }
        }
    }

    pluginManager.withPlugin("com.android.library") {
        configure<com.android.build.api.dsl.LibraryExtension> {
            buildTypes {
                getByName("debug") {
                    enableUnitTestCoverage = true
                    enableAndroidTestCoverage = true
                }
            }
        }
    }

    tasks.withType<Test> {
        configure<JacocoTaskExtension> {
            isIncludeNoLocationClasses = true
            excludes = listOf("jdk.internal.*")
        }
    }

    configurations.configureEach {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.jetbrains.kotlin" && requested.name == "kotlin-metadata-jvm") {
                useVersion(kotlinVersion)
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

tasks.register<JacocoReport>("jacocoRootReport") {
    group = "verification"
    description = "Generates an aggregated Jacoco coverage report across all modules."

    dependsOn(subprojects.map { subproject ->
        subproject.tasks.matching { it.name == "testDebugUnitTest" }
    })

    val coverageFiles = fileTree(rootDir) {
        include("**/build/outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
        include("**/build/outputs/code_coverage/debugAndroidTest/connected/*/coverage.ec")
    }
    executionData.setFrom(coverageFiles)

    val compiledClasses = fileTree(rootDir) {
        include(
            "**/build/intermediates/javac/debug/**/classes/**",
            "**/build/tmp/kotlin-classes/debug/**",
        )
        exclude(
            "**/R.class",
            "**/R$*.class",
            "**/BuildConfig.*",
            "**/Manifest*.*",
            "**/*Test*.*",
            "**/*_MembersInjector.*",
            "**/*_Factory.*",
            "**/*_HiltModules*.*",
            "**/Dagger*.*",
            "**/Hilt_*.*",
            "**/*_Impl*.*",
            "**/databinding/**",
            "**/generated/**",
        )
    }
    classDirectories.setFrom(compiledClasses)

    val mainSources = fileTree(rootDir) {
        include("**/src/main/java/**", "**/src/main/kotlin/**")
    }
    sourceDirectories.setFrom(mainSources)

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}
