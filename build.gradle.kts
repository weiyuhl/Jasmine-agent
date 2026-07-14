// Root build.gradle.kts

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.dokka)
    alias(libs.plugins.hilt.gradle) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.screenshot) apply false
    alias(libs.plugins.spotless) apply false
}

val documentedProjects =
    setOf(
        "app",
        "core:common",
        "core:database",
        "core:datastore",
        "core:designsystem",
        "core:domain",
        "core:model",
        "core:navigation",
        "core:network",
        "core:testing",
        "core:ui",
        "data:agent",
        "feature:home:api",
        "feature:home:impl",
        "native:bridge",
        "platform:background",
        "platform:files",
        "platform:notifications",
        "platform:os",
        "platform:permissions",
        "platform:telemetry",
        "benchmark",
        "test-app",
    )

val kotlinVersion = libs.versions.kotlin.get()

dependencies {
    documentedProjects.forEach { moduleName ->
        dokka(dependencyFactory.createProjectDependency(":$moduleName"))
    }
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
    val projectPath = path.removePrefix(":")
    if (projectPath in documentedProjects) {
        apply(plugin = "org.jetbrains.dokka")
    }

    configurations.configureEach {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.jetbrains.kotlin" && requested.name == "kotlin-metadata-jvm") {
                useVersion(kotlinVersion)
                because("Keep kotlin-metadata-jvm aligned so annotation processors can read Kotlin 2.4 metadata.")
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
