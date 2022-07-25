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

@Suppress("UnusedPrivateMember")
tasks {
    val setupGitHooks by creating {
        run("chmod -R +x .githook/")
        run("git config core.hooksPath .githook/")
    }
}

//
// Helpers
//

fun Task.run(command:String) = doLast {
    exec {
        commandLine(command.split(" "))
    }
}