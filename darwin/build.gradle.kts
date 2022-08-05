import org.apache.tools.ant.taskdefs.condition.Os

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
                    "-Dsonar.projectBaseDir=method",
                    "-Dsonar.verbose=true",
                    "-Dsonar.host.url=https://sonarcloud.io",
                )
            }
        }
    }

    val assembleXCFramework by creating {
        dependsOn(":common:assembleXCFramework")
    }

    val assembleDev by creating {
        dependsOn(setupSecrets)
        dependsOn(assembleXCFramework)
        fastLane("buildDev")
    }

    val assembleStg by creating{
        dependsOn(setupSecrets)
        dependsOn(assembleXCFramework)
        fastLane("buildStg")
    }

    val assemblePrd by creating{
        dependsOn(setupSecrets)
        dependsOn(assembleXCFramework)
        fastLane("buildPrd")
    }

    val build by creating {
        finalizedBy(assembleDev)
        finalizedBy(assembleStg)
        finalizedBy(assemblePrd)
    }

    listOf(
        setupSecrets,
        cleanSecrets,
        sonarqube,
        assembleXCFramework,
        assembleDev,
        assembleStg,
        assemblePrd,
        build,
    ).forEach {
        it.onlyIf { Os.isFamily(Os.FAMILY_MAC) }
    }
}

//
// Helpers
//

fun Task.fastLane(lane: String) {
    doLast {
        exec {
            environment("BUILD_TIME_HASH", System.getProperty("BUILD_TIME_HASH").toString())
            environment("MATCH_PASSWORD", env.fetch("SECRETS_PASSWORD"))
            environment("MATCH_GIT_REPO", rootProject.buildDir.resolve(".secrets").path)

            workingDir = projectDir
            executable = "bundle"
            args = listOf(
                "exec",
                "fastlane",
                "ios",
                lane,
            )
        }
    }
}