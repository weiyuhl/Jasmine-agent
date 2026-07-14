plugins { `kotlin-dsl` }

group = "com.lhzkml.jasmineagent.buildlogic"

java {
  sourceCompatibility = JavaVersion.VERSION_25
  targetCompatibility = JavaVersion.VERSION_25
}

kotlin {
  compilerOptions {
    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
  }
}

gradlePlugin {
  plugins {
    register("androidApplication") {
      id = "jasmine.android.application"
      implementationClass = "com.lhzkml.jasmineagent.buildlogic.AndroidApplicationConventionPlugin"
    }
    register("androidLibrary") {
      id = "jasmine.android.library"
      implementationClass = "com.lhzkml.jasmineagent.buildlogic.AndroidLibraryConventionPlugin"
    }
    register("androidTest") {
      id = "jasmine.android.test"
      implementationClass = "com.lhzkml.jasmineagent.buildlogic.AndroidTestConventionPlugin"
    }
    register("androidCompose") {
      id = "jasmine.android.compose"
      implementationClass = "com.lhzkml.jasmineagent.buildlogic.AndroidComposeConventionPlugin"
    }
    register("androidHilt") {
      id = "jasmine.android.hilt"
      implementationClass = "com.lhzkml.jasmineagent.buildlogic.AndroidHiltConventionPlugin"
    }
    register("androidRoom") {
      id = "jasmine.android.room"
      implementationClass = "com.lhzkml.jasmineagent.buildlogic.AndroidRoomConventionPlugin"
    }
    register("androidSerialization") {
      id = "jasmine.android.serialization"
      implementationClass = "com.lhzkml.jasmineagent.buildlogic.AndroidSerializationConventionPlugin"
    }
  }
}

dependencies {
  implementation(libs.android.gradle.plugin)
  implementation(libs.detekt.gradle.plugin)
  implementation(libs.hilt.gradle.plugin)
  implementation(libs.kotlin.compose.gradle.plugin)
  implementation(libs.kotlin.gradle.plugin)
  implementation(libs.kotlin.serialization.gradle.plugin)
  implementation(libs.ksp.gradle.plugin)
  implementation(libs.spotless.gradle.plugin)
}
