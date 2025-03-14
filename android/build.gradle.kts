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
    packaging {
        resources.excludes.add("META-INF/AL2.0")
        resources.excludes.add("META-INF/LGPL2.1")
    }
    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        isCoreLibraryDesugaringEnabled = true
    }
    buildFeatures {
        buildConfig = true
    }
    flavorDimensions += "version"
    productFlavors {
        create("foss")
        create("play")
    }
}

dependencies {
    implementation(project(":common"))
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}
