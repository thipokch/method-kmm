import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

//
// Gradle Plugins
//

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")

    /** DevOps Tooling **/
    id("org.sonarqube")                 version "3.4.0.2513"    // Code Analysis Platform
    id("io.gitlab.arturbosch.detekt")   version "1.21.0"        // Kotlin Analysis + Lint
    id("org.jetbrains.kotlinx.kover")   version "0.5.0"         // Test Coverage
}

//
// Multiplatform Targets
//

@Suppress("UnusedPrivateMember")
kotlin {
    targets {
        android()
        iosX64()
        iosArm64()
        iosSimulatorArm64()
        jvm {
            compilations.all {
                kotlinOptions.jvmTarget = "1.8"
            }
        }
        js(IR) {
            binaries.executable()
            nodejs()
        }

        val xcf = XCFramework("common")
        val darwinTargets = listOf(
            iosX64(),
            iosArm64(),
            iosSimulatorArm64()
        )
        darwinTargets.forEach {
            it.binaries.framework {
                baseName = "common"
                isStatic = true
                freeCompilerArgs += "-Xno-objc-generics"
                embedBitcode("bitcode")
                xcf.add(this)
            }
        }
    }


    sourceSets {
        val commonMain by getting {

        }
        val commonTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test:1.7.10")
            }
        }

        val androidMain by getting {
            dependsOn(commonMain)
        }
        val androidTest by getting {
            dependsOn(commonTest)
        }

        val jsMain by getting {
            dependsOn(commonMain)
        }
        val jsTest by getting {
            dependsOn(commonTest)
        }

        val jvmMain by getting {
            dependsOn(commonMain)
        }
        val jvmTest by getting {
            dependsOn(commonTest)
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val darwinMain by creating {
            dependsOn(commonMain)

            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val darwinTest by creating {
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
// DevOps Tooling Config
//

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.21.0") // Code Formatting
}

detekt {
    parallel = true
    source = files(
        "src/androidMain/kotlin/",
        "src/commonMain/kotlin/",
        "src/darwinMain/kotlin/",
        "src/jsMain/kotlin/",
        "src/jvmMain/kotlin/",
    )
}

tasks.sonarqube.dependsOn(tasks.koverVerify)

sonarqube {
    properties {
        property("sonar.projectName", "method.common")
        property("sonar.projectKey", "method.common")
        property("sonar.organization", "thipokch")
        property("sonar.host.url", "https://sonarcloud.io")

        // Code Reports
        property("sonar.kotlin.detekt.reportPaths", buildDir.resolve("reports/detekt/detekt.xml"))
        property("sonar.coverage.jacoco.xmlReportPaths", buildDir.resolve("reports/kover/report.xml"))

        // Multiplatform Targets
        property("sonar.sources", listOf(
            "src/androidMain/kotlin/",
            "src/commonMain/kotlin/",
            "src/darwinMain/kotlin/",
            "src/jsMain/kotlin/",
            "src/jvmMain/kotlin/",
        ).joinToString(","))

        property("sonar.tests", listOf(
            "src/androidTest/kotlin/",
            "src/commonTest/kotlin/",
            "src/darwinTest/kotlin/",
            "src/jsTest/kotlin/",
            "src/jvmTest/kotlin/",
        ).joinToString(","))
    }
}