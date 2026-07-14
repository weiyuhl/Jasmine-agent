plugins { id("jasmine.android.library") }

android { namespace = "com.lhzkml.jasmineagent.core.testing" }

dependencies {
  implementation(libs.androidx.test.runner)
  implementation(libs.androidx.test.rules)
  implementation(libs.hilt.android.testing)
}
