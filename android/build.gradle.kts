@file:Suppress("UnstableApiUsage")

plugins {
    id("org.jetbrains.compose")
    id("com.android.application")
    kotlin("android")
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

compose {
    val kotlinVersion = rootProject.extra["kotlin.version"].toString()

    kotlinCompilerPlugin.set("org.jetbrains.compose.compiler:compiler:${rootProject.extra["compose.compiler.version"]}")
    kotlinCompilerPluginArgs.add("suppressKotlinVersionCompatibilityCheck=${kotlinVersion}")
}
