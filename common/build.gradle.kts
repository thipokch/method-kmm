import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import org.jetbrains.kotlin.gradle.plugin.mpp.BitcodeEmbeddingMode

//
// Gradle Plugins
//

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")

    /***** DevOps Tooling *****/

    id("org.sonarqube")                 version "3.4.0.2513"    // Code Analysis Platform
    id("io.gitlab.arturbosch.detekt")   version "1.21.0"        // Kotlin Analysis + Lint
    id("org.jetbrains.kotlinx.kover")   version "0.5.0"         // Test Coverage
    id("co.touchlab.kermit")            version "1.1.3"         // Strip Logging

    /***** Code Utilities *****/

    id("io.realm.kotlin")               version "1.0.1"         // Realm Code Gen
    id("kotlin-parcelize")                                      // Parcel Code Gen
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

                /***** XCFramework Export *****/

                    // Business Logic Components (BLoCs) with routing
                export("com.arkivanov.decompose:decompose:0.7.0")
                    // State management via Model-View-Intent
                export("com.arkivanov.mvikotlin:mvikotlin-main:3.0.1")
                    // Lifecycle
                export("com.arkivanov.essenty:lifecycle:0.4.2")
                    // Logging
                export("co.touchlab:kermit:1.1.3")

                baseName = "common"
                isStatic = true
                freeCompilerArgs += "-Xno-objc-generics"

                xcf.add(this)
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {

                implementation(project(":resource"))

                /***** Architecture & Frameworks *****/

                    // Parallelism via Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                    // Dependency Injection
                implementation("io.insert-koin:koin-core:3.2.0")
                    // Business Logic Components (BLoCs) with routing
                implementation("com.arkivanov.decompose:decompose:0.7.0")
                    // State management via Model-View-Intent
                implementation("com.arkivanov.mvikotlin:mvikotlin:3.0.1")
                implementation("com.arkivanov.mvikotlin:mvikotlin-main:3.0.1")
                implementation("com.arkivanov.mvikotlin:rx:3.0.1")
                implementation("com.arkivanov.mvikotlin:mvikotlin-extensions-coroutines:3.0.1")
                implementation("com.arkivanov.mvikotlin:mvikotlin-logging:3.0.1")
                    // Lifecycle
                implementation("com.arkivanov.essenty:lifecycle:0.4.2")

                /***** Utilities *****/

                    // Date + Time
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
                    // Universally Unique Identifier Structure
                implementation("com.benasher44:uuid:0.4.1")
                    // Uniform Resource Identifier
                implementation("com.eygraber:uri-kmp:0.0.4")
                    // Semantic Versioning
                implementation("io.github.z4kn4fein:semver:1.3.3")
                    // Logging
                implementation("co.touchlab:kermit:1.1.3")
                    // DI Logging
                implementation("co.touchlab:kermit-koin:1.1.3") {
                    // See: https://github.com/touchlab/Kermit/issues/264

                    // Unsure why test is included in Main Targets
                    // Also, missing io.insert-koin:koin-test in test targets
                    // https://github.com/touchlab/Kermit/blob/463b9269be4bf85a860da5e8ac1f149d6e159ebd/kermit-koin/build.gradle.kts#L70-L90
                    exclude("org.jetbrains.kotlin", "kotlin-test-junit")
                    exclude("org.jetbrains.kotlin", "kotlin-test")
                }
            }
        }

        val commonTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test:1.7.10")
                    // Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
                    // Flow
                implementation("app.cash.turbine:turbine:0.8.0")
                    // Dependency Injection
                implementation("io.insert-koin:koin-test:3.2.0")
            }
        }

        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                    // Crashlytics
                implementation("co.touchlab:kermit-crashlytics:1.1.3")
            }
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

            dependencies {

                /***** XCFramework Export *****/

                    // Business Logic Components (BLoCs) with routing
                api("com.arkivanov.decompose:decompose:0.7.0")
                    // State management via Model-View-Intent
                api("com.arkivanov.mvikotlin:mvikotlin-main:3.0.1")
                    // implementation("co.touchlab:kermit-koin:1.1.3")
                    // See: https://github.com/touchlab/Kermit/issues/264
                    // Lifecycle
                api("com.arkivanov.essenty:lifecycle:0.4.2")
                    // Logging
                api("co.touchlab:kermit:1.1.3")
                    // Crashlytics
                implementation("co.touchlab:kermit-crashlytics:1.1.3")
            }
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val darwinTest by creating {
            dependsOn(commonTest)

            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)

            dependencies {
                // Crashlytics
                implementation("co.touchlab:kermit-crashlytics-test:1.1.3")
            }
        }

        val realmMain by creating {
            dependsOn(commonMain)

            androidMain.dependsOn(this)
            darwinMain.dependsOn(this)
            jvmMain.dependsOn(this)

            dependencies {
                implementation("io.realm.kotlin:library-base:1.4.0")
            }
        }

        val realmTest by creating {
            dependsOn(commonTest)

            // AndroidTest seems to be running on a JVM based on the following issue
            // As a workaround, we will be testing the Unit Tests under Android Instrumented Test.
            // https://github.com/realm/realm-kotlin/issues/697#issuecomment-1050425318
            // https://youtrack.jetbrains.com/issue/KT-46452
            // https://youtrack.jetbrains.com/issue/KT-42298
            // https://github.com/realm/realm-kotlin/blob/76f9654ec1fac5ad7fe2ecbe753be93b73859116/README.md
            // https://github.com/realm/realm-kotlin/pull/187

            androidTest.dependsOn(this)
            darwinTest.dependsOn(this)
            jvmTest.dependsOn(this)
        }
    }
}

android {
    compileSdk = 32
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 24
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
        "src/realmMain/kotlin/",
    )
}

tasks.sonarqube.dependsOn(tasks.koverVerify)

sonarqube.properties {
    property("sonar.projectName", "method.common")
    property("sonar.projectKey", "method.common")
    property("sonar.organization", "thipokch")
    property("sonar.host.url", "https://sonarcloud.io")

    // Code Reports
    property("sonar.kotlin.detekt.reportPaths", buildDir.resolve("reports/detekt/detekt.xml"))
    property(
        "sonar.coverage.jacoco.xmlReportPaths",
        buildDir.resolve("reports/kover/report.xml")
    )

    // Multiplatform Targets
    property("sonar.sources", listOf(
        "src/androidMain/kotlin/",
        "src/commonMain/kotlin/",
        "src/darwinMain/kotlin/",
        "src/jsMain/kotlin/",
        "src/jvmMain/kotlin/",
        "src/realmMain/kotlin/",
    ).joinToString(","))

    property("sonar.tests", listOf(
        "src/androidTest/kotlin/",
        "src/commonTest/kotlin/",
        "src/darwinTest/kotlin/",
        "src/jsTest/kotlin/",
        "src/jvmTest/kotlin/",
        "src/realmTest/kotlin/",
    ).joinToString(","))
}
