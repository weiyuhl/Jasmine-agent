plugins {
  id("jasmine.android.library")
  id("jasmine.android.serialization")
}

android { namespace = "com.lhzkml.jasmineagent.feature.home.api" }

dependencies { api(dependencyFactory.createProjectDependency(":core:navigation")) }
