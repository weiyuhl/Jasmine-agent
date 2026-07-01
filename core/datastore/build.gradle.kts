import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.detekt)
  alias(libs.plugins.spotless)
}

android {
  namespace = "com.lhzkml.jasmineagent.core.datastore"
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
    moduleName.set("jasmineagent_core_datastore")
  }
}

dependencies {
  implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.datastore.core)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.security.crypto)

  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
}
