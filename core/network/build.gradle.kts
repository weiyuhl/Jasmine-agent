plugins {
  id("jasmine.android.library")
  id("jasmine.android.serialization")
}

android { namespace = "com.lhzkml.jasmineagent.core.network" }

dependencies {
  api(libs.retrofit)
  api(libs.ktor.client.core)
  implementation(libs.ktor.client.okhttp)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.serialization.json)

  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
}
