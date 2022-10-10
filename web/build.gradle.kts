import com.android.build.gradle.internal.tasks.factory.dependsOn

//
// Project Config
//

plugins {
    id("org.jetbrains.kotlin.js")
    /** DevOps Tooling **/
    id("org.sonarqube")                 version "3.4.0.2513"    // Code Analysis Platform
    id("io.gitlab.arturbosch.detekt")   version "1.21.0"        // Kotlin Analysis + Lint
    id("org.jetbrains.kotlinx.kover")   version "0.6.1"         // Test Coverage
}

kotlin {
    js(IR) {
        binaries.executable()
        nodejs()
    }
}

dependencies {
    implementation(project(":common"))
    testImplementation(kotlin("test-js"))
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.21.0") // Code Formatting
}

//
// Tasks
//

@Suppress("UnusedPrivateMember")
tasks {

    val cleanSecrets by creating {
        doLast {
            val envDirs = listOf(
                projectDir.resolve("src/main/resources/dev"),
                projectDir.resolve("src/main/resources/stg"),
                projectDir.resolve("src/main/resources/prd"),
            )

            val envFiles = envDirs
                .map { fileTree(it) }
                .flatten()
                .filter { it.isFile }

            envFiles
                .filter { it.name == "google-services.js" }
                .forEach { delete(it) }
        }
    }

    val setupSecrets by creating {
        dependsOn(":setupSecrets")
        doLast {
            logger.info("copying .secrets/google/web into resources")
            copy {
                from(rootProject.layout.buildDirectory.dir(".secrets/google/web"))
                into(layout.projectDirectory.dir("src/main/resources"))
                include("**/*.js")
            }
        }
    }

    sonarqube.dependsOn(koverVerify)
    build.dependsOn(setupSecrets)

    val assembleDev by creating
    val assembleStg by creating
    val assemblePrd by creating

    assembleDev.dependsOn(build)
    assembleStg.dependsOn(build)
    assemblePrd.dependsOn(build)

    val buildDev by creating {
        group = "build"
        dependsOn(check)
        dependsOn(assembleDev)
    }

    val buildStg by creating {
        group = "build"
        dependsOn(check)
        dependsOn(assembleStg)
    }

    val buildPrd by creating {
        group = "build"
        dependsOn(check)
        dependsOn(assemblePrd)
    }
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

sonarqube.properties {

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
