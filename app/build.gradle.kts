import java.io.File
import java.security.KeyStore
import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

fun canReadKeystore(storeFile: File, storePassword: String, keyAlias: String): Boolean =
  listOf(KeyStore.getDefaultType(), "JKS", "PKCS12").distinct().any { storeType ->
    runCatching {
        val keyStore = KeyStore.getInstance(storeType)
        storeFile.inputStream().use { input ->
          keyStore.load(input, storePassword.toCharArray())
        }
        keyStore.containsAlias(keyAlias)
      }
      .getOrDefault(false)
  }

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

    ndk { abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86_64") }
  }

  var releaseSigningAvailable = false

  signingConfigs {
    create("release") {
      val keystorePropertiesFile = rootProject.file("keystore.properties")
      if (keystorePropertiesFile.exists()) {
        val keystoreProperties = Properties().apply { load(keystorePropertiesFile.inputStream()) }
        val configuredStoreFile = rootProject.file(keystoreProperties.getProperty("storeFile", ""))
        val configuredStorePassword = keystoreProperties.getProperty("storePassword")
        val configuredKeyAlias = keystoreProperties.getProperty("keyAlias")
        val configuredKeyPassword = keystoreProperties.getProperty("keyPassword")

        if (
          configuredStoreFile.isFile &&
            !configuredStorePassword.isNullOrBlank() &&
            !configuredKeyAlias.isNullOrBlank() &&
            !configuredKeyPassword.isNullOrBlank() &&
            canReadKeystore(configuredStoreFile, configuredStorePassword, configuredKeyAlias)
        ) {
          storeFile = configuredStoreFile
          storePassword = configuredStorePassword
          keyAlias = configuredKeyAlias
          keyPassword = configuredKeyPassword
          releaseSigningAvailable = true
        } else {
          logger.warn(
            "keystore.properties is present but does not match the configured keystore. " +
              "Release APK will be unsigned."
          )
        }
      } else {
        logger.warn("keystore.properties not found. Release APK will be unsigned.")
      }
    }
  }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      if (releaseSigningAvailable) {
        signingConfig = signingConfigs.getByName("release")
      }
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

  packaging {
    jniLibs { keepDebugSymbols += "**/libjnidispatch.so" }
    resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" }
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
}

ksp { arg("room.schemaLocation", "$projectDir/schemas") }

// Migrate from kotlinOptions to compilerOptions
kotlin {
  compilerOptions {
    jvmTarget.set(JvmTarget.JVM_17)
    moduleName.set("jasmineagent_app")
  }
}

dependencies {
  implementation(project(":core-data"))
  implementation(project(":core-database"))
  implementation(project(":core-domain"))
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
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
  implementation(libs.androidx.compose.material3.adaptive)
  implementation(libs.androidx.compose.material3.adaptive.navigation3)

  // Tooling
  debugImplementation(libs.androidx.compose.ui.tooling)
  debugImplementation(libs.androidx.compose.ui.tooling.data)

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
