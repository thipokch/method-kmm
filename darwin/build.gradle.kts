import org.openbakery.XcodeBuildTask

plugins {
    id("org.openbakery.xcode-plugin") version "0.21.0"
}

@Suppress("UnusedPrivateMember")
tasks {
    val clean by getting {
        doLast {
            delete("darwin/GoogleService-Info.plist")
        }
    }

    val swiftLint by creating {
        mkdir("build/reports/swiftlint/")
        play("swiftlint lint --reporter json " +
                "> build/reports/swiftlint/report.json")
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

    // Due to openbakery/gradle-xcodePlugin#458
    // xcodebuild is configured in taskgraph.whenReady below
    val buildDev by creating(XcodeBuildTask::class)
    val buildPre by creating(XcodeBuildTask::class)
    val buildPrd by creating(XcodeBuildTask::class)

    buildDev.dependsOn(fireDev)
    buildPre.dependsOn(firePre)
    buildPre.dependsOn(firePrd)

    // Due to openbakery/gradle-xcodePlugin#458
    // xcodebuild is configured in taskgraph.whenReady below
    gradle.taskGraph.whenReady {

        xcodebuild {
            scheme = "dev"
            target = "method"

            infoplist {
                bundleIdentifier = "ch.thipok.method.dev"
                bundleDisplayName = "Method Dev"
            }
        }
        
        if(hasTask(buildPre)) {
            xcodebuild {
                scheme = "pre"
                target = "method"

                infoplist {
                    bundleIdentifier = "ch.thipok.method.pre"
                    bundleDisplayName = "Method Pre"
                }
            }
        }

        if(hasTask(buildPrd)) {
            xcodebuild {
                scheme = "prd"
                target = "method"

                infoplist {
                    bundleIdentifier = "ch.thipok.method"
                    bundleDisplayName = "Method"
                }
            }
        }
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