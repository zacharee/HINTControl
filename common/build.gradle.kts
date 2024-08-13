import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    alias(libs.plugins.kotlin.native.cocoapods)
    alias(libs.plugins.kotlin.atomicfu)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.library)
    alias(libs.plugins.moko.resources)
    alias(libs.plugins.buildkonfig)
}

val appVersionCode: Int by rootProject.extra
val androidMinSdk: Int by rootProject.extra
val androidCompileSdk: Int by rootProject.extra

val appVersionName: String by rootProject.extra
val appPackageName: String by rootProject.extra

val javaVersion: JavaVersion by rootProject.extra

version = appVersionName

kotlin {
    androidTarget()
    jvm("desktop")

    val iosArm64 = iosArm64()
    val iosSimulatorArm64 = iosSimulatorArm64()

    listOf(iosArm64, iosSimulatorArm64).forEach {
        it.compilations.getByName("main") {
            cinterops.create("BugsnagHINT") {
                includeDirs("$projectDir/src/nativeInterop/cinterop/Bugsnag")
                definitionFile.set(file("$projectDir/src/nativeInterop/cinterop/Bugsnag.def"))
            }
        }
        it.binaries {
            framework {
                isStatic = true
                binaryOption("bundleVersion", appVersionCode.toString())
                binaryOption(
                    "bundleShortVersionString",
                    appVersionName,
                )
                binaryOption("bundleId", appPackageName)
            }
        }
    }

    targets.all {
        compilations.all {
            compileTaskProvider {
                compilerOptions {
                    freeCompilerArgs.add("-Xexpect-actual-classes")
                }
            }
        }
    }

    cocoapods {
        version = appVersionCode.toString()
        summary = "KVD21Control"
        homepage = "https://zwander.dev"
        ios.deploymentTarget = "14.0"
        osx.deploymentTarget = "10.13"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "common"
            isStatic = true
            export(libs.moko.resources)
            export(libs.nsexceptionKt.core)

            binaryOption("bundleVersion", appVersionCode.toString())
            binaryOption(
                "bundleShortVersionString",
                appVersionName,
            )
            binaryOption("bundleId", appPackageName)
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material3)
                api(compose.ui)

                api(libs.moko.resources)
                api(libs.moko.resources.compose)
                api(libs.moko.mvvm.compose)
                api(libs.moko.mvvm.flow.compose)
                api(libs.ktor.client.core)
                api(libs.ktor.client.auth)
                api(libs.ktor.client.contentNegotiation)
                api(libs.ktor.client.mock)
                api(libs.ktor.serialization.kotlinx.json)
                api(libs.kotlin.reflect)
                api(libs.korlibs.korio)
                api(libs.korlibs.klock)
                api(libs.multiplatformSettings)
                api(libs.multiplatformSettings.noArg)
                api(libs.kstore)
                api(libs.kstore.file)
                api(libs.okio)
                api(libs.koalaplot)
                api(libs.kotlinx.serialization.json)
                api(libs.kotlinx.serialization.json.okio)
                api(libs.kotlinx.coroutines)
                api(libs.semver)
            }
        }
        val nonAppleMain by creating {
            dependsOn(commonMain)
        }

        val androidMain by getting {
            dependsOn(nonAppleMain)
            dependencies {
                api(libs.androidx.appcompat)
                api(libs.androidx.activity.compose)
                api(libs.androidx.core.ktx)
                api(libs.google.material)

                api(libs.ktor.client.okhttp)
                api(libs.kotlinx.coroutines.android)
                api(libs.bugsnag.android)
                api(libs.relinker)
                api(libs.androidx.glance.appwidget)

                api(libs.taskerpluginlibrary)
                api(libs.github.api)
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

                api(libs.ktor.client.okhttp)
                api(libs.jna)
                api(libs.slf4j.jdk14)
                api(libs.bugsnag.jvm)
                api(libs.jSystemThemeDetector)
                api(libs.oshi.core)
                api(libs.appdirs)
                api(libs.kotlinx.coroutines.swing)
                api(libs.conveyor.control)
            }
        }

        val darwinMain by creating {
            dependsOn(skiaMain)
            dependencies {
                api(libs.ktor.client.darwin)
                api(libs.nsexceptionKt.bugsnag)
                api(libs.nsexceptionKt.core)
                api(libs.nserrorKt)
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

    compileSdk = androidCompileSdk

    sourceSets {
        getByName("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            java.srcDirs("build/generated/moko/androidMain/src")
        }
    }

    defaultConfig {
        minSdk = androidMinSdk
        resValue("string", "app_name", "${rootProject.extra["appName"]}")
    }
    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
    lint {
        abortOnError = false
    }

    dependencies {
        coreLibraryDesugaring(libs.desugar.jdk.libs)
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion.toString()))
}

multiplatformResources {
    resourcesPackage.set("dev.zwander.resources.common")
}

buildkonfig {
    packageName = "dev.zwander.common"
    objectName = "GradleConfig"
    exposeObjectWithName = objectName

    defaultConfigs {
        buildConfigField(STRING, "versionName", appVersionName)
        buildConfigField(STRING, "versionCode", "$appVersionCode")
        buildConfigField(STRING, "packageName", appPackageName)
        buildConfigField(STRING, "appName", "${rootProject.extra["appName"]}")
    }
}

afterEvaluate {
    try {
        exec {
            commandLine(
                "plutil",
                "-replace",
                "CFBundleShortVersionString",
                "-string",
                appVersionName,
                "../iosApp/iosApp/Info.plist",
            )
        }
    } catch (_: Throwable) {
    }

    try {
        exec {
            commandLine(
                "plutil",
                "-replace",
                "CFBundleVersion",
                "-string",
                "$appVersionCode",
                "../iosApp/iosApp/Info.plist",
            )
        }
    } catch (_: Throwable) {
    }
}
