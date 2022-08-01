import com.android.build.gradle.internal.tasks.factory.dependsOn

plugins {
    id("org.jetbrains.kotlin.js")
    /** DevOps Tooling **/
    id("org.sonarqube")                 version "3.4.0.2513"    // Code Analysis Platform
    id("io.gitlab.arturbosch.detekt")   version "1.21.0"        // Kotlin Analysis + Lint
    id("org.jetbrains.kotlinx.kover")   version "0.5.0"         // Test Coverage
}

kotlin {
    js(IR) {
        binaries.executable()
        nodejs()
    }
}

@Suppress("UnusedPrivateMember")
tasks {
    val clean by getting {
        doLast {
            delete("firebase-config.js")
        }
    }

    val fireDev by creating {
        dependsOn(clean)
        fireConfig("DEV")
    }

    val fireStg by creating {
        dependsOn(clean)
        fireConfig("STG")
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

fun Task.fireConfig(pipeline: String) =
    play("firebase apps:sdkconfig " +
            "--out firebase-config.js " +
            "WEB ${env.fetch("FIREBASE_APPID_WEB_$pipeline")} " +
            "--token ${env.fetch("FIREBASE_TOKEN")}" )

dependencies {
    implementation(project(":common"))
    testImplementation(kotlin("test-js"))
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.21.0") // Code Formatting
}

//
// DevOps Tooling Config
//

detekt {
    parallel = true
    source = files(
        "src/main/kotlin",
    )
}

tasks.sonarqube.dependsOn(tasks.koverVerify)

sonarqube {
    properties {
        property("sonar.projectName", "method.web")
        property("sonar.projectKey", "method.web")
        property("sonar.organization", "thipokch")
        property("sonar.host.url", "https://sonarcloud.io")

        // Code Reports
        property("sonar.kotlin.detekt.reportPaths", buildDir.resolve("reports/detekt/detekt.xml"))
        property("sonar.coverage.jacoco.xmlReportPaths", buildDir.resolve("reports/kover/report.xml"))

        // Sources
        property("sonar.sources", listOf(
            "src/main/kotlin",
        ).joinToString(","))

        property("sonar.tests", listOf(
            "src/test/kotlin",
        ).joinToString(","))
    }
}