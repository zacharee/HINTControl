@file:Suppress("UNUSED_VARIABLE")

import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
    id("com.android.library")
    id("dev.icerock.mobile.multiplatform-resources")
    kotlin("native.cocoapods")
    id("com.codingfeline.buildkonfig")
}

version = rootProject.extra["app_version_code"].toString()

kotlin {
    android()
    jvm("desktop") {
        jvmToolchain(rootProject.extra["java_version"].toString().toInt())
    }
    macosX64()
    macosArm64()

    val iosArm64 = iosArm64()
    val iosX64 = iosX64()
    val iosSimulatorArm64 = iosSimulatorArm64()

    val xcFramework = XCFramework("common")
    configure(listOf(iosArm64, iosX64, iosSimulatorArm64)) {
        binaries.withType(Framework::class.java) {
            isStatic = true
            baseName = "common"
            xcFramework.add(this)
        }
    }

    cocoapods {
        version = rootProject.extra["app_version_code"].toString()
        summary = "KVD21Control"
        homepage = "https://zwander.dev"
        ios.deploymentTarget = "15.2"
        osx.deploymentTarget = "10.13"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "common"
            isStatic = true
        }
        extraSpecAttributes["resources"] = "['src/commonMain/resources/**', 'src/iosMain/resources/**']"
        pod("Bugsnag")
    }

    sourceSets {
        val ktorVersion = "2.3.0"
        val coroutinesVersion = "1.6.4"

        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material3)

                api("dev.icerock.moko:resources:0.22.0")
                api("dev.icerock.moko:resources-compose:0.22.0")
                api("io.ktor:ktor-client-core:${ktorVersion}")
                api("io.ktor:ktor-client-auth:${ktorVersion}")
                api("io.ktor:ktor-client-content-negotiation:${ktorVersion}")
                api("io.ktor:ktor-serialization-kotlinx-json:${ktorVersion}")
                api("io.ktor:ktor-client-mock:${ktorVersion}")
                api("dev.icerock.moko:mvvm-compose:0.16.1")
                api("dev.icerock.moko:mvvm-flow-compose:0.16.1")
                api("org.jetbrains.kotlin:kotlin-reflect:${rootProject.extra["kotlin.version"]}")
                api("com.soywiz.korlibs.korio:korio:4.0.0")
                api("com.russhwolf:multiplatform-settings:1.0.0")
                api("com.russhwolf:multiplatform-settings-no-arg:1.0.0")
                api("org.jetbrains.kotlinx:atomicfu:0.20.2")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core") {
                    version {
                        strictly(coroutinesVersion)
                    }
                }
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.appcompat:appcompat:1.6.1")
                api("com.google.android.material:material:1.9.0")

                api("androidx.core:core-ktx:1.10.1")
                api("io.ktor:ktor-client-okhttp:${ktorVersion}")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-android") {
                    version {
                        strictly(coroutinesVersion)
                    }
                }
                api("com.bugsnag:bugsnag-android:5.30.0")
            }
        }
        val skiaMain by creating {
            dependsOn(commonMain)
            dependencies {

            }
        }
        val desktopMain by getting {
            dependsOn(skiaMain)
            dependencies {
                api(compose.preview)
                api("io.ktor:ktor-client-okhttp:${ktorVersion}")
                api("com.github.weisj:darklaf-core:3.0.2")
                api("com.github.weisj:darklaf-macos:3.0.2")
                api("net.java.dev.jna:jna:5.13.0")
                api("org.slf4j:slf4j-api:2.0.7")
                api("org.slf4j:slf4j-jdk14:2.0.7")
                api("com.bugsnag:bugsnag:3.6.4")
                api("com.github.Dansoftowner:jSystemThemeDetector:3.6")
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(skiaMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                api("io.ktor:ktor-client-darwin:${ktorVersion}")
            }
        }

        val macosX64Main by getting
        val macosArm64Main by getting
        val macosMain by creating {
            dependsOn(skiaMain)
            macosX64Main.dependsOn(this)
            macosArm64Main.dependsOn(this)
            dependencies {
                api("io.ktor:ktor-client-darwin:${ktorVersion}")
            }
        }
    }
}

android {
    namespace = "dev.zwander.common"

    compileSdk = rootProject.extra["compile_sdk"].toString().toInt()
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = rootProject.extra["min_sdk"].toString().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(rootProject.extra["java_version"].toString())
        targetCompatibility = sourceCompatibility
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(rootProject.extra["java_version"].toString()))
}

multiplatformResources {
    multiplatformResourcesPackage = "dev.zwander.resources.common"
}

buildkonfig {
    packageName = "dev.zwander.common"
    objectName = "GradleConfig"
    exposeObjectWithName = objectName

    defaultConfigs {
        buildConfigField(STRING, "versionName", "${rootProject.extra["app_version_name"]}")
        buildConfigField(STRING, "versionCode", "${rootProject.extra["app_version_code"]}")
        buildConfigField(STRING, "appName", "${rootProject.extra["app_name"]}")
    }
}
