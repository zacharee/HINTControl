plugins {
    id("org.jetbrains.compose")
    id("com.android.application")
    kotlin("android")
}

dependencies {
    implementation(project(":common"))
    implementation("androidx.activity:activity-compose:1.7.1")
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}
