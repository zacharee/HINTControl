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
}

plugins {
    alias(libs.plugins.kotlin.native.cocoapods) apply false
    alias(libs.plugins.moko.resources) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.buildkonfig) apply false
    alias(libs.plugins.kotlin.atomicfu) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.conveyor) apply false
    alias(libs.plugins.bugsnag.android) apply false
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
