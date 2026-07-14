plugins { id("jasmine.android.test") }

android {
  namespace = "com.lhzkml.jasmineagent.benchmark"
  targetProjectPath = ":app"

  defaultConfig {
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }
}

dependencies {
  implementation(libs.androidx.benchmark.macro.junit4)
  implementation(libs.androidx.test.core)
  implementation(libs.androidx.test.ext.junit)
  implementation(libs.androidx.test.runner)
  implementation(libs.androidx.test.rules)
  implementation(libs.androidx.test.uiautomator)
  implementation(libs.junit)
}
