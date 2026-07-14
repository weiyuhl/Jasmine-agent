plugins { id("jasmine.android.library") }

android { namespace = "com.lhzkml.jasmineagent.core.model" }

dependencies { implementation(dependencyFactory.createProjectDependency(":core:common")) }
