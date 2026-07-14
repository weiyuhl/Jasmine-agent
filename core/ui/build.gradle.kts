plugins {
  id("jasmine.android.library")
  id("jasmine.android.compose")
}

android { namespace = "com.lhzkml.jasmineagent.core.ui" }

dependencies {
  api(dependencyFactory.createProjectDependency(":core:designsystem"))

  val composeBom = platform(libs.androidx.compose.bom)
  implementation(composeBom)
  androidTestImplementation(composeBom)

  implementation(libs.androidx.core.ktx)

  api(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.tooling.preview)
  debugImplementation(libs.androidx.compose.ui.tooling)
}
