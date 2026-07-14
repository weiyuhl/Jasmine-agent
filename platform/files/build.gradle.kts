plugins {
  id("jasmine.android.library")
  id("jasmine.android.hilt")
}

android { namespace = "com.lhzkml.jasmineagent.platform.files" }

dependencies {
  implementation(libs.androidx.core.ktx)

  testImplementation(libs.junit)
}
