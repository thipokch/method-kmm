//
// Gradle Plugins
//

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
    /** Tools **/
    id("com.github.ben-manes.versions") version "0.42.0"        // Dependency Update Notice
    id("io.gitlab.arturbosch.detekt")   version "1.21.0"        // Kotlin Analysis + Lint
    id("org.sonarqube")                 version "3.4.0.2513"    // Code Analysis Platform
}

//
// Target Configuration
//

kotlin {
    android()
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
        }
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test:1.7.10")
            }
        }
        val androidMain by getting
        val androidTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    compileSdk = 32
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 32
    }
}

//
// Code Analysis Config
//

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.21.0") // Code Formatting
}

detekt {
    source = files(
        "src/androidMain/kotlin",
        "src/commonMain/kotlin",
        "src/iOSMain/kotlin",
    )
}

sonarqube {
    properties {
        property("sonar.projectKey", "thipokch_method")
        property("sonar.organization", "thipokch")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}