import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    alias(libs.plugins.kotlin.native.cocoapods)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.library)
    alias(libs.plugins.moko.resources)
    alias(libs.plugins.buildkonfig)
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
                export(libs.moko.resources)
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
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "commonFrameworkOld"
            isStatic = true
        }
        pod("Bugsnag")
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
                api(libs.kotlinx.atomicfu)
                api(libs.okio)
                api(libs.koalaplot)
                api(libs.kotlinx.serialization.json)
                api(libs.kotlinx.serialization.json.okio)
                api(libs.kotlinx.coroutines)
                api(libs.compose.compiler)
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
                // https://stackoverflow.com/a/73710583/5496177
                api(libs.jSystemThemeDetector.get().let { "${it.module}:${it.versionConstraint.requiredVersion}" }) {
                    exclude("net.java.dev.jna", "jna")
                }
                api(libs.oshi.core)
                api(libs.appdirs)
                api(libs.kotlinx.coroutines.swing)
            }
        }

        val darwinMain by creating {
            dependsOn(skiaMain)
            dependencies {
                api(libs.ktor.client.darwin)
                api(libs.nsexceptionKt.bugsnag)
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
    kotlinCompilerPlugin.set("org.jetbrains.compose.compiler:compiler:${libs.versions.compose.compiler.get()}")
    kotlinCompilerPluginArgs.add("suppressKotlinVersionCompatibilityCheck=${libs.versions.kotlin.get()}")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(rootProject.extra["java_version"].toString()))
}

multiplatformResources {
    resourcesPackage.set("dev.zwander.resources.common")
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

//afterEvaluate {
//    tasks.withType<SyncComposeResourcesForIosTask> {
//        dependsOn(tasks.findByName("generateMRcommonMain"))
//        dependsOn(tasks.findByName("generateMRiosSimulatorArm64Main"))
//        dependsOn(tasks.findByName("generateMRiosArm64Main"))
//    }
//}
