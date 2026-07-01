import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.android.test)
  alias(libs.plugins.detekt)
  alias(libs.plugins.spotless)
}

android {
  namespace = "com.lhzkml.jasmineagent.benchmark"
  compileSdk = 37
  targetProjectPath = ":app"

  defaultConfig {
    minSdk = 26
    targetSdk = 37
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildFeatures {
    aidl = false
    buildConfig = false
    shaders = false
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_26
    targetCompatibility = JavaVersion.VERSION_26
  }
}

kotlin {
  compilerOptions {
    jvmTarget.set(JvmTarget.JVM_26)
    moduleName.set("jasmineagent_benchmark")
  }
}

dependencies {
  implementation(libs.androidx.benchmark.macro.junit4)
  implementation(libs.androidx.test.core)
  implementation(libs.androidx.test.ext.junit)
  implementation(libs.androidx.test.runner)
  implementation(libs.androidx.test.rules)
  implementation(libs.androidx.test.uiautomator)
  implementation(libs.junit)
}
