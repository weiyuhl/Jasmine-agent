plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    `maven-publish`
}

group = "com.lhzkml.jasmine"
version = "0.1.0"

android {
    namespace = "com.lhzkml.jasmine.theme"
    compileSdk = 37

    defaultConfig {
        minSdk = 23
        aarMetadata {
            minCompileSdk = 37
        }
    }

    buildFeatures {
        compose = true
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.androidx.core.ktx)
    api(platform(libs.compose.bom))
    api(libs.compose.material3)
    api(libs.compose.ui)
    api(libs.compose.runtime)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                groupId = project.group.toString()
                artifactId = "jasmine-theme"
                version = project.version.toString()

                pom {
                    name.set("Jasmine Theme")
                    description.set("A Compose Material3-based Jasmine application theme library.")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                }
            }
        }
    }
}
