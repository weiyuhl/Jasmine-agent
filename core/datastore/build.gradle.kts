plugins { id("jasmine.android.library") }

android { namespace = "com.lhzkml.jasmineagent.core.datastore" }

dependencies {
  implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.datastore.core)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.security.crypto)

  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
}
