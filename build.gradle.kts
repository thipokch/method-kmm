buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
        classpath("com.android.tools.build:gradle:7.2.1")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

//
// Tasks
//

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

tasks.register("setup") {
    // Setup GitHooks for Precommit check
    Runtime.getRuntime().exec("chmod -R +x .githook/")
    Runtime.getRuntime().exec("git config core.hooksPath .githook/")
}