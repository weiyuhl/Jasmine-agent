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

val releaseKeystorePropertiesFile = rootProject.file("keystore.properties")
val releaseKeystoreProperties =
  Properties().apply {
    if (releaseKeystorePropertiesFile.isFile) {
      releaseKeystorePropertiesFile.inputStream().use { input -> load(input) }
    }
  }

fun Properties.hasReleaseSigningConfig(): Boolean =
  listOf("storeFile", "storePassword", "keyAlias", "keyPassword").all {
    !getProperty(it).isNullOrBlank()
  }

android {
  namespace = "com.lhzkml.jasmineagent"
  compileSdk = 37

  defaultConfig {
    applicationId = "com.lhzkml.jasmineagent"
    minSdk = 26
    targetSdk = 37
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "com.lhzkml.jasmineagent.core.testing.HiltTestRunner"

    vectorDrawables { useSupportLibrary = true }

    ndk { abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86_64") }
  }

  signingConfigs {
    create("release") {
      if (releaseKeystoreProperties.hasReleaseSigningConfig()) {
        storeFile = rootProject.file(releaseKeystoreProperties.getProperty("storeFile"))
        storePassword = releaseKeystoreProperties.getProperty("storePassword")
        keyAlias = releaseKeystoreProperties.getProperty("keyAlias")
        keyPassword = releaseKeystoreProperties.getProperty("keyPassword")
      }
    }
  }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      if (releaseKeystoreProperties.hasReleaseSigningConfig()) {
        signingConfig = signingConfigs.getByName("release")
      }
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_26
    targetCompatibility = JavaVersion.VERSION_26
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
    jvmTarget.set(JvmTarget.JVM_26)
    moduleName.set("jasmineagent_app")
  }
}

dependencies {
  implementation(project(":core:designsystem"))
  implementation(project(":core:navigation"))
  implementation(project(":feature:home:api"))
  implementation(project(":feature:home:impl"))
  implementation(project(":platform:background"))
  implementation(project(":platform:files"))
  implementation(project(":platform:notifications"))
  implementation(project(":platform:os"))
  implementation(project(":platform:permissions"))
  implementation(project(":platform:telemetry"))

  // Core Android dependencies
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)

  // Hilt Dependency Injection
  implementation(libs.hilt.android)
  compileOnly(libs.error.prone.annotations)
  testCompileOnly(libs.error.prone.annotations)
  androidTestCompileOnly(libs.error.prone.annotations)
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
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.material.icons.core)

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
  androidTestImplementation(project(":core:testing"))
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(libs.androidx.test.rules)
  androidTestImplementation(libs.hilt.android.testing)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}
