/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.hilt.gradle)
  alias(libs.plugins.ksp)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.kotlin.serialization)

  alias(libs.plugins.detekt)
  alias(libs.plugins.spotless)
}

android {
  namespace = "com.lhzkml.jasmineagent"
  compileSdk = 37

  defaultConfig {
    applicationId = "com.lhzkml.jasmineagent"
    minSdk = 23
    targetSdk = 37
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "com.lhzkml.jasmineagent.core.testing.HiltTestRunner"

    vectorDrawables { useSupportLibrary = true }
  }

  signingConfigs {
    create("release") {
      val keystorePropertiesFile = rootProject.file("keystore.properties")
      if (keystorePropertiesFile.exists()) {
        val keystoreProperties = Properties().apply { load(keystorePropertiesFile.inputStream()) }
        storeFile = rootProject.file(keystoreProperties.getProperty("storeFile"))
        storePassword = keystoreProperties.getProperty("storePassword")
        keyAlias = keystoreProperties.getProperty("keyAlias")
        keyPassword = keystoreProperties.getProperty("keyPassword")
      } else {
        logger.warn("keystore.properties not found. Release signing will be unavailable.")
      }
    }
  }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  buildFeatures {
    compose = true
    aidl = false
    buildConfig = false
    shaders = false
  }

  packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }

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
}

// Enable room auto-migrations
ksp { arg("room.schemaLocation", "$projectDir/schemas") }

// Migrate from kotlinOptions to compilerOptions
kotlin { compilerOptions { jvmTarget.set(JvmTarget.JVM_17) } }

dependencies {
  implementation(project(":core-ui"))
  implementation(project(":feature-agent"))
  implementation(project(":feature-agent-navigation"))

  // Core Android dependencies
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)

  // Hilt Dependency Injection
  implementation(libs.hilt.android)
  ksp(libs.hilt.compiler)
  kspAndroidTest(libs.hilt.compiler)
  kspTest(libs.hilt.compiler)

  // Arch Components
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.viewmodel.compose)

  // Compose
  val composeBom = platform(libs.androidx.compose.bom)
  implementation(composeBom)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.material3)

  // Tooling
  debugImplementation(libs.androidx.compose.ui.tooling)

  // Navigation
  implementation(libs.androidx.navigation3.ui)
  implementation(libs.profileinstaller)
  implementation(libs.startup.runtime)
  implementation(libs.androidx.lifecycle.viewmodel.navigation3)

  // Instrumented tests
  androidTestImplementation(composeBom)
  androidTestImplementation(project(":core-testing"))
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(libs.hilt.android.testing)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}
