import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.detekt)
  alias(libs.plugins.spotless)
}

android {
  namespace = "com.lhzkml.jasmineagent.core.network"
  compileSdk = 37

  defaultConfig {
    minSdk = 26
    consumerProguardFiles("consumer-rules.pro")
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

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_26
    targetCompatibility = JavaVersion.VERSION_26
  }
}

kotlin {
  compilerOptions {
    jvmTarget.set(JvmTarget.JVM_26)
    moduleName.set("jasmineagent_core_network")
  }
}

dependencies {
  api(libs.retrofit)
  api(libs.ktor.client.core)
  implementation(libs.ktor.client.okhttp)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.serialization.json)

  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
}
