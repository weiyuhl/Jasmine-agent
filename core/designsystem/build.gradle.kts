plugins {
  id("jasmine.android.library")
  id("jasmine.android.compose")
}

android { namespace = "com.lhzkml.jasmineagent.core.designsystem" }

dependencies {
  val composeBom = platform(libs.androidx.compose.bom)
  implementation(composeBom)

  api(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.foundation)
}
