pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "Method"
include(
    ":common",
    ":android",
    ":darwin",
    ":web"
)