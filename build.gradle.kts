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

plugins {
    /** DevOps Tooling **/
    id("com.github.ben-manes.versions") version "0.42.0"        // Dependency Update Notice
    id("co.uzzu.dotenv.gradle")         version "2.0.0"         // .env
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

@Suppress("UnusedPrivateMember")
tasks {
    val cleanDeep by creating {
        play("git clean -x -d -f -q")
    }

    val setupFire by creating {
        play("npm install -g firebase-tools")
    }

    val setupGitHooks by creating {
        play("chmod -R +x .githook/")
        play("git config core.hooksPath .githook/")
    }
}

extensions.findByName("buildScan")?.withGroovyBuilder {
    setProperty("termsOfServiceUrl", "https://gradle.com/terms-of-service")
    setProperty("termsOfServiceAgree", "yes")
}

//
// Helpers
//

fun Task.play(command:String) = doLast {
    exec {
        commandLine(command.split(" "))
    }
}