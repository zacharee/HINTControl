@file:Suppress("UnstableApiUsage")

plugins {
    alias(libs.plugins.compose)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.atomicfu)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.bugsnag.android)
}

dependencies {
    implementation(project(":common"))
}

android {
    namespace = rootProject.extra["package_name"].toString()

    compileSdk = rootProject.extra["compile_sdk"].toString().toInt()
    defaultConfig {
        applicationId = rootProject.extra["package_name"].toString()
        minSdk = rootProject.extra["min_sdk"].toString().toInt()
        targetSdk = rootProject.extra["target_sdk"].toString().toInt()
        versionCode = rootProject.extra["app_version_code"].toString().toInt()
        versionName = rootProject.extra["app_version_name"].toString()

        setProperty("archivesBaseName", "HINT_Control_$versionName")
    }
    val compatibility = JavaVersion.toVersion(rootProject.extra["java_version"].toString())
    compileOptions {
        sourceCompatibility = compatibility
        targetCompatibility = compatibility
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}
