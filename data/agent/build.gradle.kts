plugins {
  id("jasmine.android.library")
  id("jasmine.android.hilt")
}

android {
  namespace = "com.lhzkml.jasmineagent.core.data"

  testFixtures { enable = true }
}

dependencies {
  implementation(dependencyFactory.createProjectDependency(":core:domain"))
  implementation(dependencyFactory.createProjectDependency(":core:database"))
  implementation(dependencyFactory.createProjectDependency(":native:bridge"))
  implementation(libs.kotlinx.coroutines.android)

  testImplementation(dependencyFactory.createProjectDependency(":core:domain"))
  testFixturesApi(dependencyFactory.createProjectDependency(":core:database"))
  testFixturesApi(dependencyFactory.createProjectDependency(":core:domain"))
  testFixturesApi(libs.hilt.android)
  testFixturesApi(libs.kotlinx.coroutines.android)

  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
}
