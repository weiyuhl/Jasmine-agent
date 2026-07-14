package com.lhzkml.jasmineagent.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.TestExtension
import com.diffplug.gradle.spotless.SpotlessExtension
import com.google.devtools.ksp.gradle.KspExtension
import dev.detekt.gradle.Detekt
import dev.detekt.gradle.extensions.DetektExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

internal const val CompileSdk = 37
internal const val MinSdk = 26
internal const val TargetSdk = 37
internal const val DetektJvmTarget = "22"

class AndroidApplicationConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) =
    with(target) {
      pluginManager.apply("com.android.application")
      configureQuality()
      extensions.configure<ApplicationExtension> {
        compileSdk = CompileSdk

        defaultConfig {
          minSdk = MinSdk
          targetSdk = TargetSdk
          vectorDrawables { useSupportLibrary = true }
        }

        compileOptions {
          sourceCompatibility = JavaVersion.VERSION_26
          targetCompatibility = JavaVersion.VERSION_26
        }

        buildFeatures {
          aidl = false
          buildConfig = false
          shaders = false
        }

        lint {
          checkReleaseBuilds = true
          abortOnError = true
          warningsAsErrors = true
          disable.add("OldTargetApi")
          disable.add("GradleDependency")
          disable.add("TypographyFractions")
          disable.add("TypographyQuotes")
          disable.add("Aligned16KB")
        }

        buildTypes {
          getByName("debug") {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
          }
        }
      }
      configureKotlinAndroid()
    }
}

class AndroidLibraryConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) =
    with(target) {
      pluginManager.apply("com.android.library")
      configureQuality()
      extensions.configure<LibraryExtension> {
        compileSdk = CompileSdk

        defaultConfig {
          minSdk = MinSdk
          consumerProguardFiles("consumer-rules.pro")
        }

        compileOptions {
          sourceCompatibility = JavaVersion.VERSION_26
          targetCompatibility = JavaVersion.VERSION_26
        }

        buildFeatures {
          aidl = false
          buildConfig = false
          shaders = false
        }

        lint {
          abortOnError = true
          warningsAsErrors = true
          disable.add("OldTargetApi")
          disable.add("GradleDependency")
          disable.add("Aligned16KB")
        }

        buildTypes {
          getByName("debug") {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
          }
        }
      }
      configureKotlinAndroid()
    }
}

class AndroidTestConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) =
    with(target) {
      pluginManager.apply("com.android.test")
      configureQuality()
      extensions.configure<TestExtension> {
        compileSdk = CompileSdk

        defaultConfig {
          minSdk = MinSdk
          targetSdk = TargetSdk
        }

        compileOptions {
          sourceCompatibility = JavaVersion.VERSION_26
          targetCompatibility = JavaVersion.VERSION_26
        }

        buildFeatures {
          aidl = false
          buildConfig = false
          shaders = false
        }
      }
      configureKotlinAndroid()
    }
}

class AndroidComposeConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) =
    with(target) {
      pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
      pluginManager.withPlugin("com.android.application") {
        extensions.configure<ApplicationExtension> { buildFeatures { compose = true } }
      }
      pluginManager.withPlugin("com.android.library") {
        extensions.configure<LibraryExtension> { buildFeatures { compose = true } }
      }
    }
}

class AndroidHiltConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) =
    with(target) {
      pluginManager.apply("com.google.dagger.hilt.android")
      pluginManager.apply("com.google.devtools.ksp")
      dependencies {
        add("implementation", libs.findLibrary("hilt-android").get())
        add("ksp", libs.findLibrary("hilt-compiler").get())
      }
    }
}

class AndroidRoomConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) =
    with(target) {
      pluginManager.apply("com.google.devtools.ksp")
      extensions.configure<KspExtension> { arg("room.schemaLocation", "$projectDir/schemas") }
      dependencies { add("ksp", libs.findLibrary("androidx-room-compiler").get()) }
    }
}

class AndroidSerializationConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    target.pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")
  }
}

private fun Project.configureQuality() {
  pluginManager.apply("dev.detekt")
  pluginManager.apply("com.diffplug.spotless")
  pluginManager.apply("jacoco")

  extensions.configure<DetektExtension> {
    buildUponDefaultConfig.set(true)
    allRules.set(false)
    config.setFrom(rootProject.file("detekt-config.yml"))
  }
  tasks.withType<Detekt>().configureEach { jvmTarget.set(DetektJvmTarget) }

  extensions.configure<SpotlessExtension> {
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

  tasks.withType<Test>().configureEach {
    extensions.configure<JacocoTaskExtension> {
      isIncludeNoLocationClasses = true
      excludes = listOf("jdk.internal.*")
    }
  }
}

private fun Project.configureKotlinAndroid() {
  extensions.configure<KotlinAndroidProjectExtension> {
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_26)
      moduleName.set(kotlinModuleName)
    }
  }
}

private val Project.libs
  get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

private val Project.kotlinModuleName: String
  get() =
    when (path) {
      ":data:agent" -> "jasmineagent_core_data"
      ":feature:home:api" -> "jasmineagent_feature_home_navigation"
      ":feature:home:impl" -> "jasmineagent_feature_home"
      ":native:bridge" -> "jasmineagent_core_rust"
      else -> "jasmineagent_${path.removePrefix(":").replace(':', '_').replace('-', '_')}"
    }
