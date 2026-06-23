import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.ksp)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.screenshot)

  alias(libs.plugins.detekt)
  alias(libs.plugins.spotless)
}

android {
  namespace = "com.lhzkml.jasmineagent.feature.agent"
  compileSdk = 37
  experimentalProperties["android.experimental.enableScreenshotTest"] = true

  defaultConfig {
    minSdk = 23

    testInstrumentationRunner = "com.lhzkml.jasmineagent.core.testing.HiltTestRunner"
    consumerProguardFiles("consumer-rules.pro")
  }

  buildFeatures {
    compose = true
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
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
}

kotlin {
  compilerOptions {
    jvmTarget.set(JvmTarget.JVM_17)
    moduleName.set("jasmineagent_feature_agent")
  }
}

val rustHostLibraryName =
  if (System.getProperty("os.name").startsWith("Windows")) "jasmine_core.dll"
  else if (System.getProperty("os.name").startsWith("Mac")) "libjasmine_core.dylib"
  else "libjasmine_core.so"

dependencies {
  implementation(project(":core:domain"))
  implementation(project(":core:ui"))
  val composeBom = platform(libs.androidx.compose.bom)
  implementation(composeBom)
  androidTestImplementation(composeBom)
  implementation(project(":feature:agent:api"))

  androidTestImplementation(project(":core:testing"))

  // Core Android dependencies
  implementation(libs.androidx.activity.compose)

  // Arch Components
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)

  // Compose
  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.tooling.preview)

  // Navigation
  implementation(libs.androidx.navigation3.runtime)

  // Tooling
  debugImplementation(libs.androidx.compose.ui.tooling)
  screenshotTestImplementation(libs.androidx.compose.ui.tooling)
  screenshotTestImplementation(libs.screenshot.validation.api)
  // Instrumented tests
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  debugImplementation(libs.androidx.compose.ui.test.manifest)

  // Hilt Dependency Injection
  implementation(libs.hilt.android)
  ksp(libs.hilt.compiler)
  // Hilt and instrumented tests.
  androidTestImplementation(libs.hilt.android.testing)
  kspAndroidTest(libs.hilt.compiler)
  // Hilt and Robolectric tests.
  testImplementation(libs.hilt.android.testing)
  kspTest(libs.hilt.compiler)

  // Local tests: jUnit, coroutines, Android runner
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testRuntimeOnly(libs.kotlin.jna)

  // Instrumented tests: jUnit rules and runners
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.test.runner)
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
