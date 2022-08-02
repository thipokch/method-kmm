//
// Tasks
//

@Suppress("UnusedPrivateMember")
tasks {
    val setupSecrets by creating {
        dependsOn(":setupSecrets")
        doLast {
            logger.info("copying .secrets/google/darwin into darwin")
            copy {
                from(rootProject.layout.buildDirectory.dir(".secrets/google/darwin"))
                into(layout.projectDirectory)
                include("**/*.plist")
            }
        }
    }

    val cleanSecrets by creating {
        doLast {
            val envDirs = listOf(
                projectDir.resolve("dev"),
                projectDir.resolve("stg"),
                projectDir.resolve("prd"),
            )

            val envFiles = envDirs
                .map { fileTree(it) }
                .flatten()
                .filter { it.isFile }

            envFiles
                .filter { it.name == "GoogleService-Info.plist" }
                .forEach { delete(it) }
        }
    }

    val sonarqube by creating {
        doLast {
            exec {
                workingDir = projectDir
                executable = "sonar-scanner"
                args = listOf(
                    "-Dsonar.organization=thipokch",
                    "-Dsonar.projectName=method.darwin",
                    "-Dsonar.projectKey=method.darwin",
                    "-Dsonar.projectBaseDir=darwin",
                    "-Dsonar.verbose=true",
                    "-Dsonar.host.url=https://sonarcloud.io",
                )
            }
        }
    }

    val buildDev by creating {
        fastLane("buildDev")
    }
    val buildStg by creating{
        fastLane("buildStg")
    }
    val buildPrd by creating{
        fastLane("buildPrd")
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

fun Task.fastLane(lane: String) {
    doLast {
        exec {
            environment("MATCH_PASSWORD", env.fetch("SECRETS_PASSWORD"))
            environment("MATCH_GIT_REPO", rootProject.buildDir.resolve(".secrets").path)

            workingDir = projectDir
            executable = "bundle"

            args = listOfNotNull(
                "exec",
                "fastlane",
                "ios",
                lane,
            )
        }
    }
}