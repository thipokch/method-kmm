pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "Method"
include(":common")
include(":android")
include(":darwin")
include(":web")