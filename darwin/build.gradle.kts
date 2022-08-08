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

    val assembleDev by creating {
        dependsOn(setupSecrets)
        dependsOn(":common:assembleCommonReleaseXCFramework")
        dependsOn(":resource:assembleResourceReleaseXCFramework")
        fastBuild("buildDev")
    }

    val assembleStg by creating{
        dependsOn(setupSecrets)
        dependsOn(":common:assembleCommonReleaseXCFramework")
        dependsOn(":resource:assembleResourceReleaseXCFramework")
        fastBuild("buildStg")
    }

    val assemblePrd by creating{
        dependsOn(setupSecrets)
        dependsOn(":common:assembleCommonReleaseXCFramework")
        dependsOn(":resource:assembleResourceReleaseXCFramework")
        fastBuild("buildPrd")
    }

    val build by creating {
        finalizedBy(assembleDev)
        finalizedBy(assembleStg)
        finalizedBy(assemblePrd)
    }

    val deployStg by creating{
        fastDeploy("deployStg")
    }

    val deployDev by creating{
        fastDeploy("deployDev")
    }

    val deployPrd by creating{
        fastDeploy("deployPrd")
    }

    listOf(
        setupSecrets,
        cleanSecrets,
        sonarqube,
        assembleDev,
        assembleStg,
        assemblePrd,
        deployStg,
        deployPrd,
        build,
    ).forEach {
        it.onlyIf { Os.isFamily(Os.FAMILY_MAC) }
    }
}

//
// Helpers
//

fun Task.fastBuild(lane: String) {
    doLast {
        println("Build number: ${System.getProperty("BUILD_TIME_HASH")}")
        exec {
            environment(
                "FL_BUILD_NUMBER_BUILD_NUMBER",
                System.getProperty("BUILD_TIME_HASH").toString())
            environment(
                "MATCH_PASSWORD",
                env.fetch("SECRETS_PASSWORD"))
            environment(
                "MATCH_GIT_URL",
                rootProject.buildDir.resolve(".secrets").path)

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

fun Task.fastDeploy(lane: String) {
    doLast {
        exec {
            environment("APP_STORE_CONNECT_API_KEY_KEY_ID", env.fetch("ASC_KEY_ID"))
            environment("APP_STORE_CONNECT_API_KEY_ISSUER_ID", env.fetch("ASC_ISSUER_ID"))
            environment("APP_STORE_CONNECT_API_KEY_KEY", env.fetch("ASC_KEY_CONTENT"))

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