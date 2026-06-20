plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    `maven-publish`
}

group = "com.lhzkml.jasmine"
version = "0.1.0"

android {
    namespace = "com.lhzkml.jasmine.components"
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
    api(project(":theme"))
    api(platform(libs.compose.bom))
    api(libs.compose.runtime)
    api(libs.compose.ui)
    api(libs.compose.foundation)
    api(libs.compose.animation)
    api(libs.graphics.shapes)
    api(libs.androidx.window.core)
    api(libs.navigation3.ui)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.collection)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.window)
    implementation(libs.navigationevent.compose)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                groupId = project.group.toString()
                artifactId = "jasmine-components"
                version = project.version.toString()

                pom {
                    name.set("Jasmine Components")
                    description.set("Jasmine Compose components forked from AndroidX Material3 sources.")
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
