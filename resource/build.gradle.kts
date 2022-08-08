import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
    
    id("dev.icerock.mobile.multiplatform-resources") version "0.20.1" // Resources
}

//
// Assets
//

multiplatformResources {
    multiplatformResourcesClassName = "Resource"
    multiplatformResourcesPackage = "ch.thipok.method.resource"
    disableStaticFrameworkWarning = true
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

        val xcf = XCFramework("resource")
        val darwinTargets = listOf(
            iosX64(),
            iosArm64(),
            iosSimulatorArm64()
        )
        darwinTargets.forEach {
            it.binaries.framework {

                /***** XCFramework Export *****/

                export("dev.icerock.moko:resources:0.20.1")
                export("dev.icerock.moko:graphics:0.9.0")

                baseName = "resource"
                isStatic = true
                freeCompilerArgs += "-Xno-objc-generics"

                xcf.add(this)
            }
        }
    }

    sourceSets {

        val androidMain by getting
        val jsMain by getting
        val jvmMain by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting

        val commonMain by getting {
            dependencies {
                api("dev.icerock.moko:resources:0.20.1")
                api("dev.icerock.moko:graphics:0.9.0")
            }
             androidMain.dependsOn(this)
             jsMain.dependsOn(this)
             jvmMain.dependsOn(this)
             iosX64Main.dependsOn(this)
             iosArm64Main.dependsOn(this)
             iosSimulatorArm64Main.dependsOn(this)
        }

        val androidTest by getting
        val jsTest by getting
        val jvmTest by getting
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting

        val commonTest by getting {
            dependencies {
                api("dev.icerock.moko:resources-test:0.20.1")
            }
            androidTest.dependsOn(this)
            jsTest.dependsOn(this)
            jvmTest.dependsOn(this)
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
        minSdk = 24
        targetSdk = 32
    }
}