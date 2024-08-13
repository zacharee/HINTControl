@file:Suppress("UnstableApiUsage")

plugins {
    alias(libs.plugins.compose)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.atomicfu)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.bugsnag.gradle)
}

val appVersionCode: Int by rootProject.extra
val androidMinSdk: Int by rootProject.extra
val androidTargetSdk: Int by rootProject.extra
val androidCompileSdk: Int by rootProject.extra

val appVersionName: String by rootProject.extra
val appPackageName: String by rootProject.extra

val javaVersion: JavaVersion by rootProject.extra

dependencies {
    implementation(project(":common"))
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}

android {
    namespace = appPackageName

    compileSdk = androidCompileSdk
    defaultConfig {
        applicationId = appPackageName
        minSdk = androidMinSdk
        targetSdk = androidTargetSdk
        versionCode = appVersionCode
        versionName = appVersionName

        setProperty("archivesBaseName", "HINT_Control_$versionName")
    }
    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    buildFeatures {
        buildConfig = true
    }
}
