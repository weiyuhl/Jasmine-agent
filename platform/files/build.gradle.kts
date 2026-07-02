import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.ksp)
  alias(libs.plugins.detekt)
  alias(libs.plugins.spotless)
}

android {
  namespace = "com.lhzkml.jasmineagent.platform.files"
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
    moduleName.set("jasmineagent_platform_files")
  }
}

dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(libs.hilt.android)
  ksp(libs.hilt.compiler)

  testImplementation(libs.junit)
}
