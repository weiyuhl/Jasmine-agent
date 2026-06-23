import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.android.test)
  alias(libs.plugins.hilt.gradle)
  alias(libs.plugins.ksp)

  alias(libs.plugins.detekt)
  alias(libs.plugins.spotless)
}

android {
  namespace = "com.lhzkml.jasmineagent.test.app"
  compileSdk = 37
  targetProjectPath = ":app"

  defaultConfig {
    minSdk = 23
    targetSdk = 37

    testInstrumentationRunner = "com.lhzkml.jasmineagent.core.testing.HiltTestRunner"
  }

  buildFeatures {
    aidl = false
    buildConfig = false
    shaders = false
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
}

kotlin {
  compilerOptions {
    jvmTarget.set(JvmTarget.JVM_17)
    moduleName.set("jasmineagent_test_app")
  }
}

dependencies {
  implementation(project(":app"))
  val composeBom = platform(libs.androidx.compose.bom)
  implementation(composeBom)
  implementation(project(":core:domain"))
  implementation(project(":core:testing"))
  implementation(project(":data:agent"))
  implementation(testFixtures(project(":data:agent")))
  implementation(project(":feature:agent:api"))
  implementation(project(":feature:agent:impl"))

  // Testing
  implementation(libs.androidx.test.core)

  // Hilt and instrumented tests.
  implementation(libs.hilt.android)
  implementation(libs.hilt.android.testing)
  ksp(libs.hilt.compiler)

  // Compose
  implementation(libs.androidx.compose.ui.test.junit4)
  implementation(libs.androidx.test.uiautomator)
}
