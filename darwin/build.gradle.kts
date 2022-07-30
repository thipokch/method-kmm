val isCI = env.fetch("CI", "false").toLowerCase() == "true"
val tempDir = file(env.fetch("RUNNER_TEMP", "$projectDir/temp"))

@Suppress("UnusedPrivateMember")
tasks {

    val clean by creating {
        doLast {
            delete("darwin/GoogleService-Info.plist")
        }
//        finalizedBy(xcodebuildClean) Circular Deps
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
}

//
// Helpers
//

fun Task.play(command:String) = doLast {
    exec {
        commandLine(command.split(" "))
    }
}

fun Task.fireConfig(environment: String) =
    play("firebase apps:sdkconfig " +
            "--out darwin/GoogleService-Info.plist " +
            "IOS ${env.fetch("FIREBASE_APPID_IOS_$environment")} " +
            "--token ${env.fetch("FIREBASE_TOKEN")}" )

fun Task.appleCertificate() {
    if (isCI) {
        val cerEncoded = env.fetch("APPLE_DIS_CERTIFICATE")
        val cerPath = tempDir.resolve("DIS.p12").toString()
        play("echo -n $cerEncoded | base64 --decode --output $cerPath")
    }
}

fun Task.appleProfile(scheme: String) {
    if (isCI) {
        val schemeString = scheme.toUpperCase()
        val profileEncoded = env.fetch("APPLE_${schemeString}_PROVISIONING_PROFILE")
        val profilePath = tempDir.resolve("${schemeString}.mobileprovision").toString()
        play("echo -n $profileEncoded | base64 --decode --output $profilePath")
    }
}