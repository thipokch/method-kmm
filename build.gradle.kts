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
        classpath("com.android.tools.build:gradle:7.2.1")
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
                            "-k",
                            env.fetch("SECRETS_PASSWORD"),
                            "-out",
                            it.name.removeSuffix(".enc"),
                            "-in",
                            it.name.toString(),
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