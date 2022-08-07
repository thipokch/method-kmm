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
    ":resource",
    ":android",
    ":darwin",
    ":web"
)