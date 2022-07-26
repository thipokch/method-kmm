plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    /** DevOps Tooling **/
    id("org.sonarqube")                 version "3.4.0.2513"    // Code Analysis Platform
    id("io.gitlab.arturbosch.detekt")   version "1.21.0"        // Kotlin Analysis + Lint
    id("org.jetbrains.kotlinx.kover")   version "0.5.0"         // Test Coverage
}

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
        getByName("release") {
            isMinifyEnabled = false
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
            delete("google-services.json")
        }
    }

    val fireDev by creating {
        dependsOn(clean)
        fireConfig("DEV")
    }

    val firePre by creating {
        dependsOn(clean)
        fireConfig("PRE")
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
            "--out google-services.json " +
            "ANDROID ${env.fetch("FIREBASE_APPID_ANDROID_$pipeline")} " +
            "--token ${env.fetch("FIREBASE_TOKEN")}" )


//
// DevOps Tooling Config
//

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.21.0") // Code Formatting
}

detekt {
    parallel = true
    source = files(
        "common/src/androidMain/kotlin",
        "common/src/commonMain/kotlin",
        "common/src/darwinMain/kotlin",
    )
}

tasks.sonarqube {
    dependsOn(tasks.koverVerify)
}

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