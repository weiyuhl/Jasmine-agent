pluginManagement {
    repositories {
        maven { url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        google()
        mavenCentral()
    }
}

includeBuild("jasmine-theme") {
    dependencySubstitution {
        substitute(module("com.lhzkml.jasmine:jasmine-theme")).using(project(":theme"))
        substitute(module("com.lhzkml.jasmine:jasmine-components")).using(project(":components"))
    }
}

rootProject.name = "Multimodule template"

include(":app")
include(":core:database")
include(":core:domain")
include(":core:testing")
include(":core:ui")
include(":data:agent")
include(":feature:agent:api")
include(":feature:agent:impl")
include(":native:bridge")
include(":test-app")
