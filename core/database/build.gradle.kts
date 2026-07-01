import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.ksp)

  alias(libs.plugins.detekt)
  alias(libs.plugins.spotless)
}

android {
  namespace = "com.lhzkml.jasmineagent.core.database"
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

ksp { arg("room.schemaLocation", "$projectDir/schemas") }

kotlin {
  compilerOptions {
    jvmTarget.set(JvmTarget.JVM_26)
    moduleName.set("jasmineagent_core_database")
  }
}

dependencies {
  // Arch Components
  implementation(libs.androidx.room.runtime)
  implementation(libs.androidx.room.ktx)
  implementation(libs.sqlcipher)
  implementation(libs.sqldelight.runtime)
  implementation(libs.sqldelight.android.driver)
  ksp(libs.androidx.room.compiler)
  implementation(libs.androidx.core.ktx)
  implementation(libs.security.crypto)
  implementation(libs.hilt.android)
  ksp(libs.hilt.compiler)

  // Testing
  testImplementation(libs.junit)
  testImplementation(libs.robolectric)
  testImplementation(libs.androidx.test.core)
  testImplementation(libs.androidx.test.ext.junit)
}
