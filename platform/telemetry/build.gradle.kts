plugins {
  id("jasmine.android.library")
  id("jasmine.android.hilt")
}

android { namespace = "com.lhzkml.jasmineagent.platform.telemetry" }

dependencies { testImplementation(libs.junit) }
