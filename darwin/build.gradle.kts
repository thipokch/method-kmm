import org.openbakery.XcodeBuildTask

plugins {
    id("org.openbakery.xcode-plugin") version "0.21.0"
}

xcodebuild {
    target = "darwin"
}

@Suppress("UnusedPrivateMember")
tasks {
    val clean by getting {
        doLast {
            delete("darwin/GoogleService-Info.plist")
        }
    }

    val fireDev by creating {
        dependsOn(clean)
        fireConfig("DEV")
    }

    val firePre by creating {
        dependsOn(clean)
        fireConfig("PRE")
    }

    val firePrd by creating {
        dependsOn(clean)
        fireConfig("PRD")
    }

    val buildDev by creating(XcodeBuildTask::class) {
        dependsOn(fireDev)
        scheme = "dev"
    }

    val buildPre by creating(XcodeBuildTask::class) {
        dependsOn(firePre)
        scheme = "pre"
    }

    val buildPrd by creating(XcodeBuildTask::class) {
        dependsOn(firePrd)
        scheme = "prd"
    }
}

//
// Helpers
//

fun Task.play(command:String) = doLast {
    exec {
        commandLine(command.split(" "))
    }
}

fun Task.fireConfig(pipeline: String) =
    play("firebase apps:sdkconfig " +
            "--out darwin/GoogleService-Info.plist " +
            "IOS ${env.fetch("FIREBASE_APPID_IOS_$pipeline")} " +
            "--token ${env.fetch("FIREBASE_TOKEN")}" )