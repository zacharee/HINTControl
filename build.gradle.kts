allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        maven("https://jitpack.io")
    }
}

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    dependencies {
        classpath("dev.icerock.moko:resources-generator:${rootProject.extra["moko.resources.version"]}")
        classpath("com.bugsnag:bugsnag-android-gradle-plugin:8.1.0")
        classpath("com.codingfeline.buildkonfig:buildkonfig-gradle-plugin:0.15.1")
        classpath("org.jetbrains.kotlinx:atomicfu-gradle-plugin:${rootProject.extra["kotlin.atomicfu.version"]}")
        classpath("dev.hydraulic:gradle-plugin:1.9")
    }
}

plugins {
    kotlin("multiplatform") apply false
    kotlin("android") apply false
    id("com.android.application") apply false
    id("com.android.library") apply false
    id("org.jetbrains.compose") apply false
}

apply(plugin = "kotlinx-atomicfu")

tasks.register("clearIOSOutput") {
    doLast {
        delete("iosApp/output")
        mkdir("iosApp/output")
        mkdir("iosApp/output/Payload/HINT Control.app")
    }
}

tasks.register("buildXCArchive") {
    dependsOn(":clearIOSOutput")

    doLast {
        exec {
            commandLine(
                "xcodebuild",
                "archive",
                "-workspace", "iosApp/iosApp.xcworkspace",
                "-sdk", "iphoneos",
                "-scheme", "iosApp",
                "-archivePath", "iosApp/output/iosApp.xcarchive",
                "-destination", "generic/platform=iOS",
            )
        }
    }
}

tasks.register("moveXCArchive") {
    dependsOn(":buildXCArchive")

    doLast {
        exec {
            commandLine(
                "mv", "iosApp/output/iosApp.xcarchive/Products/Applications/HINT Control.app",
                "iosApp/output/Payload",
            )
        }
    }
}

tasks.register("buildIPA") {
    dependsOn(":moveXCArchive")

    doLast {
        exec {
            setWorkingDir("iosApp/output")
            commandLine(
                "zip",
                "-r",
                "HINT Control.ipa",
                "Payload",
            )
        }
    }
}
