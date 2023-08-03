@file:Suppress("UNUSED_VARIABLE")

import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.compose.experimental.uikit.tasks.SyncComposeResourcesForIosTask

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
    androidTarget()
    jvm("desktop") {
        jvmToolchain(rootProject.extra["java_version"].toString().toInt())
    }
    macosX64()
    macosArm64()

    val iosArm64 = iosArm64()
    val iosX64 = iosX64()
    val iosSimulatorArm64 = iosSimulatorArm64()

    listOf(
        iosArm64,
        iosX64,
        iosSimulatorArm64,
    ).forEach { iosTarget ->
        iosTarget.binaries {
            framework {
                baseName = "common"
                isStatic = true
                export("dev.icerock.moko:resources:${rootProject.extra["moko.resources.version"]}")
            }
        }
    }

//    val xcFramework = XCFramework("common")
//    configure(listOf(iosArm64, iosX64, iosSimulatorArm64)) {
//        binaries.withType(Framework::class.java) {
//            isStatic = true
//            baseName = "common"
//            linkerOpts += "-ld64"
//            xcFramework.add(this)
//
//            freeCompilerArgs += listOf("-Xoverride-konan-properties=osVersionMin.ios_arm32=14;osVersionMin.ios_arm64=14;osVersionMin.ios_x64=14")
//        }
//    }

    cocoapods {
        version = rootProject.extra["app_version_code"].toString()
        summary = "KVD21Control"
        homepage = "https://zwander.dev"
        ios.deploymentTarget = "14.0"
        osx.deploymentTarget = "10.13"
        framework {
            baseName = "commonFrameworkOld"
        }
//        podfile = project.file("../iosApp/Podfile")
//        framework {
//            baseName = "common"
//            isStatic = true
//            export("dev.icerock.moko:resources:${rootProject.extra["moko.resources.version"]}")
//        }
        pod("Bugsnag")
    }

    sourceSets {
        val ktorVersion = "2.3.2"
        val coroutinesVersion = "1.7.2"
        val slf4jVersion = "2.0.7"
        val multiplatformSettingsVersion = "1.0.0"
        val mokoMvvmVersion = "0.16.1"
        val mokoResourcesVersion = rootProject.extra["moko.resources.version"].toString()
        val korlibsVersion = "4.0.9"
        val kstoreVersion = "0.6.0"

        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material3)
                api(compose.ui)

                api("dev.icerock.moko:resources:${mokoResourcesVersion}")
                api("dev.icerock.moko:resources-compose:${mokoResourcesVersion}")
                api("io.ktor:ktor-client-core:${ktorVersion}")
                api("io.ktor:ktor-client-auth:${ktorVersion}")
                api("io.ktor:ktor-client-content-negotiation:${ktorVersion}")
                api("io.ktor:ktor-serialization-kotlinx-json:${ktorVersion}")
                api("io.ktor:ktor-client-mock:${ktorVersion}")
                api("dev.icerock.moko:mvvm-compose:${mokoMvvmVersion}")
                api("dev.icerock.moko:mvvm-flow-compose:${mokoMvvmVersion}")
                api("org.jetbrains.kotlin:kotlin-reflect:${rootProject.extra["kotlin.version"]}")
                api("com.soywiz.korlibs.korio:korio:${korlibsVersion}")
                api("com.soywiz.korlibs.klock:klock:${korlibsVersion}")
                api("com.russhwolf:multiplatform-settings:${multiplatformSettingsVersion}")
                api("com.russhwolf:multiplatform-settings-no-arg:${multiplatformSettingsVersion}")
                api("io.github.xxfast:kstore:$kstoreVersion")
                api("io.github.xxfast:kstore-file:$kstoreVersion")
                api("org.jetbrains.kotlinx:atomicfu:${rootProject.extra["kotlin.atomicfu.version"]}")
                api("com.squareup.okio:okio:3.4.0")
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core") {
                    version {
                        strictly(coroutinesVersion)
                    }
                }
            }
        }
        val nonAppleMain by creating {
            dependsOn(commonMain)
        }

        val androidMain by getting {
            dependsOn(nonAppleMain)
            dependencies {
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.activity:activity-compose:1.7.2")
                api("androidx.core:core-ktx:1.10.1")
                api("com.google.android.material:material:1.9.0")

                api("io.ktor:ktor-client-okhttp:${ktorVersion}")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-android") {
                    version {
                        strictly(coroutinesVersion)
                    }
                }
                api("com.bugsnag:bugsnag-android:5.30.0")
                api("com.getkeepsafe.relinker:relinker:1.4.5")
                api("androidx.glance:glance-appwidget:1.0.0-rc01")
            }
        }
        val skiaMain by creating {
            dependsOn(commonMain)
        }
        val desktopMain by getting {
            dependsOn(skiaMain)
            dependsOn(nonAppleMain)
            dependencies {
                api(compose.preview)
                api(compose.desktop.currentOs)

                api("io.ktor:ktor-client-okhttp:${ktorVersion}")
                api("com.github.weisj:darklaf-core:3.0.2")
                api("net.java.dev.jna:jna:5.13.0")
                api("org.slf4j:slf4j-api:${slf4jVersion}")
                api("org.slf4j:slf4j-jdk14:${slf4jVersion}")
                api("com.bugsnag:bugsnag:3.7.0")
                api("com.github.Dansoftowner:jSystemThemeDetector:3.8")
                api("com.github.oshi:oshi-core:6.4.4")
                api("net.harawata:appdirs:1.2.1")
            }
        }

        val darwinMain by creating {
            dependsOn(skiaMain)
            dependencies {
                api("io.ktor:ktor-client-darwin:${ktorVersion}")
                api("com.rickclephas.kmp:nsexception-kt-bugsnag:0.1.9")
                api("com.rickclephas.kmp:nserror-kt:0.1.0")
            }
        }

        val iosX64Main by getting {
            resources.srcDirs("build/generated/moko/iosX64Main/src")
        }
        val iosArm64Main by getting {
            resources.srcDirs("build/generated/moko/iosArm64Main/src")
        }
        val iosSimulatorArm64Main by getting {
            resources.srcDirs("build/generated/moko/iosSimulatorArm64Main/src")
        }
        val iosMain by creating {
            dependsOn(darwinMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }

        val macosX64Main by getting
        val macosArm64Main by getting
        val macosMain by creating {
            dependsOn(darwinMain)
            macosX64Main.dependsOn(this)
            macosArm64Main.dependsOn(this)
        }
    }
}

android {
    namespace = "dev.zwander.common"

    compileSdk = rootProject.extra["compile_sdk"].toString().toInt()

    sourceSets {
        getByName("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            java.srcDirs("build/generated/moko/androidMain/src")
        }
    }

    defaultConfig {
        minSdk = rootProject.extra["min_sdk"].toString().toInt()
    }
    val compatibility = JavaVersion.toVersion(rootProject.extra["java_version"].toString())
    compileOptions {
        sourceCompatibility = compatibility
        targetCompatibility = compatibility
    }
    lint {
        abortOnError = false
    }
}

compose {
    val kotlinVersion = rootProject.extra["kotlin.version"].toString()

    kotlinCompilerPlugin.set("org.jetbrains.compose.compiler:compiler:${rootProject.extra["compose.compiler.version"]}")
    kotlinCompilerPluginArgs.add("suppressKotlinVersionCompatibilityCheck=${kotlinVersion}")
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
        buildConfigField(STRING, "packageName", "${rootProject.extra["package_name"]}")
    }
}

afterEvaluate {
    tasks.withType<SyncComposeResourcesForIosTask> {
        dependsOn(tasks.findByName("generateMRcommonMain"))
        dependsOn(tasks.findByName("generateMRiosSimulatorArm64Main"))
        dependsOn(tasks.findByName("generateMRiosArm64Main"))
        dependsOn(tasks.findByName("generateMRiosX64Main"))
    }
}
