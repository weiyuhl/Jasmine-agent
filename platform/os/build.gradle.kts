plugins {
  id("jasmine.android.library")
  id("jasmine.android.hilt")
}

android { namespace = "com.lhzkml.jasmineagent.platform.os" }

dependencies {
  implementation(libs.androidx.annotation)

  testImplementation(libs.junit)
}
