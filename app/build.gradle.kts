import java.util.Properties

plugins {
  id("jasmine.android.application")
  id("jasmine.android.compose")
  id("jasmine.android.hilt")
  id("jasmine.android.serialization")
}

val releaseKeystorePropertiesFile = rootProject.file("keystore.properties")
val releaseKeystoreProperties =
  Properties().apply {
    if (releaseKeystorePropertiesFile.isFile) {
      releaseKeystorePropertiesFile.inputStream().use { input -> load(input) }
    }
  }

fun Properties.hasReleaseSigningConfig(): Boolean =
  listOf("storeFile", "storePassword", "keyAlias", "keyPassword").all {
    !getProperty(it).isNullOrBlank()
  }

android {
  namespace = "com.lhzkml.jasmineagent"

  defaultConfig {
    applicationId = "com.lhzkml.jasmineagent"
    versionCode = 1
    versionName = "1.0"
    testInstrumentationRunner = "com.lhzkml.jasmineagent.core.testing.HiltTestRunner"
    ndk { abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86_64") }
  }

  signingConfigs {
    create("release") {
      if (releaseKeystoreProperties.hasReleaseSigningConfig()) {
        storeFile = rootProject.file(releaseKeystoreProperties.getProperty("storeFile"))
        storePassword = releaseKeystoreProperties.getProperty("storePassword")
        keyAlias = releaseKeystoreProperties.getProperty("keyAlias")
        keyPassword = releaseKeystoreProperties.getProperty("keyPassword")
      }
    }
  }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      if (releaseKeystoreProperties.hasReleaseSigningConfig()) {
        signingConfig = signingConfigs.getByName("release")
      }
    }
  }

  packaging {
    jniLibs { keepDebugSymbols += "**/libjnidispatch.so" }
    resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" }
  }
}

dependencies {
  implementation(dependencyFactory.createProjectDependency(":core:designsystem"))
  implementation(dependencyFactory.createProjectDependency(":core:navigation"))
  implementation(dependencyFactory.createProjectDependency(":feature:home:api"))
  implementation(dependencyFactory.createProjectDependency(":feature:home:impl"))
  implementation(dependencyFactory.createProjectDependency(":platform:background"))
  implementation(dependencyFactory.createProjectDependency(":platform:files"))
  implementation(dependencyFactory.createProjectDependency(":platform:notifications"))
  implementation(dependencyFactory.createProjectDependency(":platform:os"))
  implementation(dependencyFactory.createProjectDependency(":platform:permissions"))
  implementation(dependencyFactory.createProjectDependency(":platform:telemetry"))

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)

  compileOnly(libs.error.prone.annotations)
  testCompileOnly(libs.error.prone.annotations)
  androidTestCompileOnly(libs.error.prone.annotations)
  kspAndroidTest(libs.hilt.compiler)
  kspTest(libs.hilt.compiler)

  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.viewmodel.compose)

  val composeBom = platform(libs.androidx.compose.bom)
  implementation(composeBom)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.material.icons.core)

  debugImplementation(libs.androidx.compose.ui.tooling)
  debugImplementation(libs.androidx.compose.ui.tooling.data)

  implementation(libs.androidx.navigation3.ui)
  implementation(libs.profileinstaller)
  implementation(libs.startup.runtime)
  implementation(libs.androidx.lifecycle.viewmodel.navigation3)

  androidTestImplementation(composeBom)
  androidTestImplementation(dependencyFactory.createProjectDependency(":core:testing"))
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(libs.androidx.test.rules)
  androidTestImplementation(libs.hilt.android.testing)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}
