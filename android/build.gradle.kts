import com.android.build.gradle.internal.tasks.factory.dependsOn

//
// Project Config
//

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    /** DevOps Tooling **/
    id("org.sonarqube")                 version "3.4.0.2513"    // Code Analysis Platform
    id("io.gitlab.arturbosch.detekt")   version "1.21.0"        // Kotlin Analysis + Lint
    id("org.jetbrains.kotlinx.kover")   version "0.6.0"         // Test Coverage
}

@Suppress("UnusedPrivateMember")
android {
    compileSdk = 32

    defaultConfig {
        applicationId = "ch.thipok.method.android"
        minSdk = 24
        targetSdk = 32
        versionCode = System.getProperty("BUILD_TIME_HASH").toInt()
        versionName = "1.0"
    }

    buildTypes {
        val release by getting {
            isMinifyEnabled = true
        }

        val debug by getting {
            isMinifyEnabled = false
        }
    }

    flavorDimensions += "environment"

    productFlavors {
        val dev by creating {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            resValue("string", "app_name", "Method Dev")
        }

        val stg by creating {
            dimension = "environment"
            applicationIdSuffix = ".stg"
            resValue("string", "app_name", "Method Stg")
        }

        val prd by creating {
            dimension = "environment"
            resValue( "string", "app_name", "Method")
        }
    }
}

dependencies {
    implementation(project(":common"))
    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    /** Testing **/
    implementation("org.jetbrains.kotlin:kotlin-test-junit:1.7.10")
    implementation("junit:junit:4.13.2")
    /** DevOps Tooling **/
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
                projectDir.resolve("src/dev"),
                projectDir.resolve("src/stg"),
                projectDir.resolve("src/prd"),
            )

            val envFiles = envDirs
                .map { fileTree(it) }
                .flatten()
                .filter { it.isFile }

            envFiles
                .filter { it.name == "google-services.json" }
                .forEach { delete(it) }
        }
    }

    val setupSecrets by creating {
        dependsOn(":setupSecrets")
        doLast {
            logger.info("copying .secrets/google/android into src")
            copy {
                from(rootProject.layout.buildDirectory.dir(".secrets/google/android"))
                into(layout.projectDirectory.dir("src"))
                include("**/*.json")
            }
        }
    }

    val buildDev by creating {
        group = "build"
        dependsOn(check)
        dependsOn("assembleDev")
    }

    val buildStg by creating {
        group = "build"
        dependsOn(check)
        dependsOn("assembleStg")
    }

    val buildPrd by creating {
        group = "build"
        dependsOn(check)
        dependsOn("assemblePrd")
    }

    sonarqube.dependsOn(koverVerify)
    build.dependsOn(setupSecrets)
}

//
// DevOps Tooling Config
//

detekt {
    parallel = true
    source = files(
        "src/main/java",
    )
}

sonarqube.properties {
    property("sonar.projectName", "method.android")
    property("sonar.projectKey", "method.android")
    property("sonar.organization", "thipokch")
    property("sonar.host.url", "https://sonarcloud.io")

    // Code Reports
    property("sonar.kotlin.detekt.reportPaths", buildDir.resolve("reports/detekt/detekt.xml"))
    property("sonar.coverage.jacoco.xmlReportPaths", buildDir.resolve("reports/kover/report.xml"))
}
