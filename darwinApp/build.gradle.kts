import org.openbakery.XcodeBuildTask

plugins {
    id("org.openbakery.xcode-plugin") version "0.21.0"
}

xcodebuild {
    target = "darwinApp"
}

tasks {
    val buildDev by creating(XcodeBuildTask::class) {
        scheme = "dev"
    }

    val buildPre by creating(XcodeBuildTask::class) {
        scheme = "pre"
    }

    val buildPrd by creating(XcodeBuildTask::class) {
        scheme = "prd"
    }
}


