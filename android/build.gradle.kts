import com.android.build.gradle.internal.tasks.factory.dependsOn

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    /** DevOps Tooling **/
    id("org.sonarqube")                 version "3.4.0.2513"    // Code Analysis Platform
    id("io.gitlab.arturbosch.detekt")   version "1.21.0"        // Kotlin Analysis + Lint
    id("org.jetbrains.kotlinx.kover")   version "0.5.0"         // Test Coverage
}

@Suppress("UnusedPrivateMember")
android {
    compileSdk = 32

    defaultConfig {
        applicationId = "ch.thipok.method.android"
        minSdk = 21
        targetSdk = 32
        versionCode = 1
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

        val pre by creating {
            dimension = "environment"
            applicationIdSuffix = ".pre"
            resValue("string", "app_name", "Method Pre")
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
}

@Suppress("UnusedPrivateMember")
tasks {
    val clean by getting {
        doLast {
            delete("src/dev/google-services.json")
            delete("src/pre/google-services.json")
            delete("src/prd/google-services.json")
        }
    }

    val fireDev by creating {
        fireConfig("DEV")
    }

    val firePre by creating {
        fireConfig("PRE")
    }

    val firePrd by creating {
        fireConfig("PRD")
    }

    val buildDev by creating
    val buildPre by creating
    val buildPrd by creating

    afterEvaluate {
        // Variants only available after evaluate
        val assembleDev by getting
        val assemblePre by getting
        val assemblePrd by getting

        assembleDev.dependsOn(fireDev)
        assemblePre.dependsOn(firePre)
        assemblePrd.dependsOn(firePrd)

        buildDev.dependsOn(assembleDev)
        buildPre.dependsOn(assemblePre)
        buildPrd.dependsOn(assemblePrd)
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

fun Task.fireConfig(pipeline: String) {
    delete("src/${pipeline.toLowerCase()}/google-services.json")
    mkdir("src/${pipeline.toLowerCase()}")
    play("firebase apps:sdkconfig " +
            "--out src/${pipeline.toLowerCase()}/google-services.json " +
            "ANDROID ${env.fetch("FIREBASE_APPID_ANDROID_${pipeline.toUpperCase()}")} " +
            "--token ${env.fetch("FIREBASE_TOKEN")}" )
}

//
// DevOps Tooling Config
//

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.21.0") // Code Formatting
}

detekt {
    parallel = true
    source = files(
        "src/main/java",
    )
}

tasks.sonarqube.dependsOn(tasks.koverVerify)

sonarqube {
    properties {
        property("sonar.projectName", "method.android")
        property("sonar.projectKey", "method.android")
        property("sonar.organization", "thipokch")
        property("sonar.host.url", "https://sonarcloud.io")

        // Code Reports
        property("sonar.kotlin.detekt.reportPaths", buildDir.resolve("reports/detekt/detekt.xml"))
        property("sonar.coverage.jacoco.xmlReportPaths", buildDir.resolve("reports/kover/report.xml"))
    }
}