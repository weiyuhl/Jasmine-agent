plugins {
  id("jasmine.android.library")
  id("jasmine.android.compose")
  id("jasmine.android.hilt")
  id("jasmine.android.serialization")
  alias(libs.plugins.screenshot)
}

android {
  namespace = "com.lhzkml.jasmineagent.feature.agent"
  experimentalProperties["android.experimental.enableScreenshotTest"] = true

  defaultConfig {
    testInstrumentationRunner = "com.lhzkml.jasmineagent.core.testing.HiltTestRunner"
  }
}

dependencies {
  implementation(dependencyFactory.createProjectDependency(":data:agent"))
  implementation(dependencyFactory.createProjectDependency(":core:domain"))
  implementation(dependencyFactory.createProjectDependency(":core:designsystem"))
  implementation(dependencyFactory.createProjectDependency(":core:navigation"))
  implementation(dependencyFactory.createProjectDependency(":feature:home:api"))

  val composeBom = platform(libs.androidx.compose.bom)
  implementation(composeBom)
  androidTestImplementation(composeBom)

  androidTestImplementation(dependencyFactory.createProjectDependency(":core:testing"))

  implementation(libs.androidx.activity.compose)

  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)

  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.tooling.preview)

  implementation(libs.androidx.navigation3.runtime)

  debugImplementation(libs.androidx.compose.ui.tooling)
  screenshotTestImplementation(libs.androidx.compose.ui.tooling)
  screenshotTestImplementation(libs.screenshot.validation.api)
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  debugImplementation(libs.androidx.compose.ui.test.manifest)

  androidTestImplementation(libs.hilt.android.testing)
  kspAndroidTest(libs.hilt.compiler)
  testImplementation(libs.hilt.android.testing)
  kspTest(libs.hilt.compiler)

  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)

  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(libs.androidx.test.rules)
}
