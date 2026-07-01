import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.ksp)
  alias(libs.plugins.detekt)
  alias(libs.plugins.spotless)
}

android {
  namespace = "com.lhzkml.jasmineagent.core.domain"
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
    moduleName.set("jasmineagent_core_domain")
  }
}

val rustHostLibraryName =
  if (System.getProperty("os.name").startsWith("Windows")) "jasmine_core.dll"
  else if (System.getProperty("os.name").startsWith("Mac")) "libjasmine_core.dylib"
  else "libjasmine_core.so"

dependencies {
  implementation(project(":native:bridge"))
  implementation(libs.hilt.android)
  ksp(libs.hilt.compiler)
  implementation(libs.kotlinx.coroutines.android)

  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testRuntimeOnly(libs.kotlin.jna)
}

tasks.withType<Test>().configureEach {
  dependsOn(":native:bridge:buildRustHost")
  systemProperty(
    "jna.library.path",
    rootProject.layout.projectDirectory
      .dir("native/bridge/build/rustHost/release")
      .asFile
      .absolutePath,
  )
  systemProperty(
    "uniffi.component.jasmine_core.libraryOverride",
    rootProject.layout.projectDirectory
      .file("native/bridge/build/rustHost/release/$rustHostLibraryName")
      .asFile
      .absolutePath,
  )
}
