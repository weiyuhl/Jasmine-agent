plugins {
  id("jasmine.android.library")
  id("jasmine.android.hilt")
}

android { namespace = "com.lhzkml.jasmineagent.core.domain" }

dependencies {
  api(dependencyFactory.createProjectDependency(":core:model"))
  implementation(libs.kotlinx.coroutines.android)

  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
}
