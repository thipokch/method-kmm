val isCI = env.fetch("CI", "false").toLowerCase() == "true"
val tempDir = file(env.fetch("RUNNER_TEMP", "$projectDir/temp"))

@Suppress("UnusedPrivateMember")
tasks {

    val clean by creating {
        doLast {
            delete("darwin/GoogleService-Info.plist")
        }
    }

    val sonarscan by creating {
        play("sonar-scanner " +
                "-Dsonar.organization=thipokch " +
                "-Dsonar.projectName=method.darwin " +
                "-Dsonar.projectKey=method.darwin " +
                "-Dsonar.projectBaseDir=darwin " +
                "-Dsonar.verbose=true " +
                "-Dsonar.host.url=https://sonarcloud.io")
    }

    val fireDev by creating {
        fireConfig("DEV")
    }

    val fireStg by creating {
        fireConfig("STG")
    }

    val firePrd by creating {
        fireConfig("PRD")
    }

    val buildDev by creating {
        dependsOn(fireDev)
        play("fastlane ios buildDev")
    }
    val buildStg by creating{
        dependsOn(fireStg)
        play("fastlane ios buildStg")
    }
    val buildPrd by creating{
        dependsOn(firePrd)
        play("fastlane ios buildPrd")
    }

    val build by creating {
        finalizedBy(buildDev)
        finalizedBy(buildStg)
        finalizedBy(buildPrd)
    }
}

//
// Helpers
//

fun Task.play(command:String) = doLast {
    exec {
        workingDir(projectDir)
        commandLine(command.split(" "))
    }
}

fun Task.fireConfig(environment: String) {
    delete("darwin/GoogleService-Info.plist")
    play("firebase apps:sdkconfig " +
            "--out darwin/GoogleService-Info.plist " +
            "IOS ${env.fetch("FIREBASE_APPID_IOS_$environment")} " +
            "--token ${env.fetch("FIREBASE_TOKEN")}" )
}