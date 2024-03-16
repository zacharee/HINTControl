import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.compose.resources.ios.SyncComposeResourcesForIosTask

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

    val iosArm64 = iosArm64()
    val iosSimulatorArm64 = iosSimulatorArm64()

    listOf(
        iosArm64,
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

    targets.all {
        compilations.all {
            compilerOptions.configure {
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }

    cocoapods {
        version = rootProject.extra["app_version_code"].toString()
        summary = "KVD21Control"
        homepage = "https://zwander.dev"
        ios.deploymentTarget = "14.0"
        osx.deploymentTarget = "10.13"
        framework {
            baseName = "commonFrameworkOld"
        }
        pod("Bugsnag")
    }

    sourceSets {
        val ktorVersion = "2.3.9"
        val coroutinesVersion = "1.8.0"
        val slf4jVersion = "2.0.12"
        val multiplatformSettingsVersion = "1.1.1"
        val mokoMvvmVersion = "0.16.1"
        val mokoResourcesVersion = rootProject.extra["moko.resources.version"].toString()
        val korlibsVersion = "4.0.10"
        val kstoreVersion = "0.7.1"

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
                api("com.squareup.okio:okio:3.8.0")
                api("io.github.koalaplot:koalaplot-core:0.4.0")
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
                api("org.jetbrains.kotlinx:kotlinx-serialization-json-okio:1.6.3")
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
                api("androidx.activity:activity-compose:1.8.2")
                api("androidx.core:core-ktx:1.12.0")
                api("com.google.android.material:material:1.11.0")

                api("io.ktor:ktor-client-okhttp:${ktorVersion}")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-android") {
                    version {
                        strictly(coroutinesVersion)
                    }
                }
                api("com.bugsnag:bugsnag-android:6.2.0")
                api("com.getkeepsafe.relinker:relinker:1.4.5")
                api("androidx.glance:glance-appwidget:1.0.0")
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
                api("net.java.dev.jna:jna:5.14.0")
                api("org.slf4j:slf4j-api:${slf4jVersion}")
                api("org.slf4j:slf4j-jdk14:${slf4jVersion}")
                api("com.bugsnag:bugsnag:3.7.1")
                api("com.github.Dansoftowner:jSystemThemeDetector:3.8")
                api("com.github.oshi:oshi-core:6.5.0")
                api("net.harawata:appdirs:1.2.2")
            }
        }

        val darwinMain by creating {
            dependsOn(skiaMain)
            dependencies {
                api("io.ktor:ktor-client-darwin:${ktorVersion}")
                api("com.rickclephas.kmp:nsexception-kt-bugsnag:0.1.16")
                api("com.rickclephas.kmp:nserror-kt:0.1.0")
            }
        }

        val iosArm64Main by getting {
            resources.srcDirs("build/generated/moko/iosArm64Main/src")
        }
        val iosSimulatorArm64Main by getting {
            resources.srcDirs("build/generated/moko/iosSimulatorArm64Main/src")
        }
        val iosMain by creating {
            dependsOn(darwinMain)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
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
    }
}
