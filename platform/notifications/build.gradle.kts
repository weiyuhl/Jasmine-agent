plugins {
  id("jasmine.android.library")
  id("jasmine.android.hilt")
}

android { namespace = "com.lhzkml.jasmineagent.platform.notifications" }

dependencies {
  implementation(dependencyFactory.createProjectDependency(":platform:os"))
  implementation(libs.androidx.core.ktx)

  testImplementation(libs.junit)
}
