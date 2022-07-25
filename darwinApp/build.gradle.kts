plugins {
    id("org.openbakery.xcode-plugin") version "0.21.0"
}

xcodebuild {
    target = "darwinApp"
}

val buildDev by tasks.creating(org.openbakery.XcodeBuildTask::class) {
    scheme = "dev"
}

val buildPre by tasks.creating(org.openbakery.XcodeBuildTask::class) {
    scheme = "pre"
}

val buildPrd by tasks.creating(org.openbakery.XcodeBuildTask::class) {
    scheme = "prd"
}