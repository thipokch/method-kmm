import org.ajoberstar.grgit.Credentials
import org.ajoberstar.grgit.Grgit

//
// Project Config
//

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
        classpath("com.android.tools.build:gradle:7.2.2")
    }
}

plugins {
    /** DevOps Tooling **/
    id("org.ajoberstar.grgit")          version "5.0.0"         // Git
    id("com.github.ben-manes.versions") version "0.42.0"        // Dependency Update Notice
    id("co.uzzu.dotenv.gradle")         version "2.0.0"         // .env
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }

    tasks.create("allDeps", DependencyReportTask::class)
}

extensions.findByName("buildScan")?.withGroovyBuilder {
    setProperty("termsOfServiceUrl", "https://gradle.com/terms-of-service")
    setProperty("termsOfServiceAgree", "yes")
}

//
// Tasks
//

@Suppress("UnusedPrivateMember")
tasks {

    val cleanSecrets by creating {
        doLast {
            delete(buildDir.resolve(".secrets/"))
        }
    }

    val cleanDeep by creating {
        dependsOn(cleanSecrets)
        doLast {
            grgit.clean {
                directories = true
                ignore = false
            }
        }
    }

    val setupSecrets by creating {

        // 1 - Clean secret repository

        dependsOn(cleanSecrets)

        doLast {
            exec {
                commandLine = listOf(
                    "bash",
                    "-c",
                    "openssl version"
                )
            }

        // 2 - Clone secret repository

            val secretDir = buildDir.resolve(".secrets/")
            Grgit.clone {
                dir = secretDir
                uri = env.fetch("SECRETS_REPO")
                credentials = Credentials(env.fetch("GRGIT_USER"))
            }

        // 3 - Decode and decrypt files with SECRETS_PASSWORD

            fileTree(secretDir)
                .filter { it.name.contains(".enc") }
                .filter { it.isFile }
                .map {
                    exec {
                        workingDir = it.parentFile
                        executable = "openssl"
                        args = listOf(
                            "aes-256-cbc",
                            "-d",
                            "-a",
                            "-v",
                            "-k",
                            env.fetch("SECRETS_PASSWORD"),
                            "-out",
                            it.name.removeSuffix(".enc"),
                            "-in",
                            it.name,
                            "-md",
                            "md5",
                        )
                    }
                }
        }
    }

    val setupGitHooks by creating {
        play("chmod -R +x .githook/")
        play("git config core.hooksPath .githook/")
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

//
// Build Time Hash
//

val now = java.time.Instant.now()
val buildDate : String = java.time.format.DateTimeFormatter
    .ofPattern("yyMMdd")
    .withZone(java.time.ZoneOffset.UTC)
    .format(now)

val nowHour = now.atZone(java.time.ZoneOffset.UTC).hour
val nowMin = now.atZone(java.time.ZoneOffset.UTC).minute
val buildTimeHash : String = "%02d".format( (nowHour * 4) + (nowMin / 15))

System.setProperty("BUILD_TIME_HASH", buildDate + buildTimeHash)

println("Set BUILD_TIME_HASH to $buildDate$buildTimeHash")
