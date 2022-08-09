import android.databinding.tool.ext.toCamelCase
import org.apache.tools.ant.taskdefs.condition.Os

//
// Tasks
//

@Suppress("UnusedPrivateMember")
tasks {
    val setupSecrets by creating {
        dependsOn(":setupSecrets")
        doLast {
            logger.info("Copying .secrets/google/darwin into darwin")
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
        group = "check"
        doLast {
            exec {
                workingDir = projectDir
                executable = "sonar-scanner"
                args = listOf(
                    "-Dsonar.organization=thipokch",
                    "-Dsonar.projectName=method.darwin",
                    "-Dsonar.projectKey=method.darwin",
                    "-Dsonar.projectBaseDir=method",
                    "-Dsonar.host.url=https://sonarcloud.io",
                    // "-Dsonar.verbose=true",
                )
            }
        }
    }

    val preAssemble by creating {
        dependsOn(setupSecrets)
        dependsOn(":common:assembleCommonReleaseXCFramework")
        dependsOn(":resource:assembleResourceReleaseXCFramework")
    }

    val assembleDev by creating {
        dependsOn(setupSecrets)
        dependsOn(preAssemble)
        fastBuild("dev")
    }

    val assembleStg by creating {
        dependsOn(setupSecrets)
        dependsOn(preAssemble)
        fastBuild("stg")
    }

    val assemblePrd by creating{
        dependsOn(setupSecrets)
        dependsOn(preAssemble)
        fastBuild("prd")
    }

    val deployStg by creating{
        fastDeploy("stg")
    }

    val deployDev by creating{
        fastDeploy("dev")
    }

    val deployPrd by creating{
        fastDeploy("prd")
    }

    val buildDev by creating {
        group = "build"
        dependsOn(sonarqube)
        dependsOn(assembleDev)
    }

    val buildStg by creating {
        group = "build"
        dependsOn(sonarqube)
        dependsOn(assembleStg)
    }

    val buildPrd by creating {
        group = "build"
        dependsOn(sonarqube)
        dependsOn(assemblePrd)
    }

    val build by creating {
        group = "build"
        finalizedBy(buildDev)
        finalizedBy(buildStg)
        finalizedBy(buildPrd)
    }

    listOf(
        setupSecrets,
        cleanSecrets,
        sonarqube,
        assembleDev,
        assembleStg,
        assemblePrd,
        deployDev,
        deployStg,
        deployPrd,
        buildDev,
        buildStg,
        buildPrd,
        build,
    ).forEach {
        it.onlyIf { Os.isFamily(Os.FAMILY_MAC) }
    }
}

//
// Helpers
//

fun Task.fastBuild(environment: String) {
    doLast {
        println("Build number: ${System.getProperty("BUILD_TIME_HASH")}")
        exec {
            environment("BUILD_TIME_HASH",              System.getProperty("BUILD_TIME_HASH").toString())
            environment("FL_BUILD_NUMBER_BUILD_NUMBER", System.getProperty("BUILD_TIME_HASH").toString())

            environment("MATCH_STORAGE_MODE",           "git")
            environment("MATCH_READONLY",               env.fetch("CI"))
            environment("MATCH_PASSWORD",               env.fetch("SECRETS_PASSWORD"))
            environment("MATCH_GIT_URL",                rootProject.buildDir.resolve(".secrets").path)

            environment("GYM_SCHEME",                   environment)
            environment("GYM_BUILDLOG_PATH",            buildDir.resolve("outputs/logs/$environment").path)
            environment("GYM_OUTPUT_DIRECTORY",         buildDir.resolve("outputs/ipa/$environment").path)


            workingDir = projectDir
            executable = "bundle"
            args = listOf(
                "exec",
                "fastlane",
                "ios",
                "build${environment.toCamelCase()}",
            )
        }
    }
}

fun Task.fastDeploy(environment: String) {
    doLast {
        exec {
            environment("BUILD_TIME_HASH",              System.getProperty("BUILD_TIME_HASH").toString())
            environment("FL_BUILD_NUMBER_BUILD_NUMBER", System.getProperty("BUILD_TIME_HASH").toString())

            val asc = "APP_STORE_CONNECT_API_KEY_"

            environment(asc + "IS_KEY_CONTENT_BASE64",   "true")
            environment(asc + "IN_HOUSE",                "true")
            environment(asc + "KEY_ID",                  env.fetch("ASC_KEY_ID"))
            environment(asc + "ISSUER_ID",               env.fetch("ASC_ISSUER_ID"))
            environment(asc + "KEY",                     env.fetch("ASC_KEY_CONTENT"))

            val ipa = buildDir.resolve("outputs/ipa/$environment/method.ipa").path

            environment("DELIVER_IPA_PATH",              ipa)
            environment("DELIVER_FORCE",                 "true")

            environment("PILOT_IPA",                     ipa)
            environment("DEMO_ACCOUNT_REQUIRED",         "false")
            environment("PILOT_NOTIFY_EXTERNAL_TESTERS", "true")
            environment("PILOT_BETA_APP_FEEDBACK",       "me@thipok.ch")
            environment("PILOT_BETA_APP_DESCRIPTION",    "Experiment with your thought.")

            workingDir = projectDir
            executable = "bundle"
            args = listOf(
                "exec",
                "fastlane",
                "ios",
                "deploy${environment.toCamelCase()}",
            )
        }
    }
}