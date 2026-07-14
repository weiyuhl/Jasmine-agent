plugins {
  id("jasmine.android.library")
  id("jasmine.android.compose")
  id("jasmine.android.serialization")
}

android { namespace = "com.lhzkml.jasmineagent.core.navigation" }

dependencies {
  implementation(dependencyFactory.createProjectDependency(":core:common"))
  api(libs.androidx.navigation3.runtime)
  implementation(libs.kotlinx.serialization.core)
  implementation(libs.kotlinx.serialization.json)
}
