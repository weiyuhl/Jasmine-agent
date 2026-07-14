plugins {
  id("jasmine.android.test")
  id("jasmine.android.hilt")
}

android {
  namespace = "com.lhzkml.jasmineagent.test.app"
  targetProjectPath = ":app"

  defaultConfig {
    testInstrumentationRunner = "com.lhzkml.jasmineagent.core.testing.HiltTestRunner"
  }
}

dependencies {
  implementation(dependencyFactory.createProjectDependency(":app"))
  val composeBom = platform(libs.androidx.compose.bom)
  implementation(composeBom)
  implementation(dependencyFactory.createProjectDependency(":core:domain"))
  implementation(dependencyFactory.createProjectDependency(":core:testing"))
  implementation(dependencyFactory.createProjectDependency(":data:agent"))
  implementation(testFixtures(dependencyFactory.createProjectDependency(":data:agent")))
  implementation(dependencyFactory.createProjectDependency(":feature:home:api"))
  implementation(dependencyFactory.createProjectDependency(":feature:home:impl"))

  implementation(libs.androidx.test.core)
  implementation(libs.androidx.test.rules)

  implementation(libs.hilt.android.testing)

  implementation(libs.androidx.compose.ui.test.junit4)
  implementation(libs.androidx.test.uiautomator)
}
