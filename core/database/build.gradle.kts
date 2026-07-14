plugins {
  id("jasmine.android.library")
  id("jasmine.android.hilt")
  id("jasmine.android.room")
}

android { namespace = "com.lhzkml.jasmineagent.core.database" }

dependencies {
  implementation(libs.androidx.room.runtime)
  implementation(libs.androidx.room.ktx)
  implementation(libs.sqlcipher)
  implementation(libs.sqldelight.runtime)
  implementation(libs.sqldelight.android.driver)
  implementation(libs.androidx.core.ktx)
  implementation(libs.security.crypto)

  testImplementation(libs.junit)
  testImplementation(libs.robolectric)
  testImplementation(libs.androidx.test.core)
  testImplementation(libs.androidx.test.ext.junit)
}
