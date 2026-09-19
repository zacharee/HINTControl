extra["androidCompileSdk"] = 37
extra["androidTargetSdk"] = 37
extra["androidMinSdk"] = 24
extra["javaVersion"] = JavaVersion.VERSION_21

extra["appVersionCode"] = 60
extra["appVersionName"] = "1.17.3"

extra["appGroup"] = "dev.zwander"
extra["appPackageName"] = "dev.zwander.arcadyankvd21control"
extra["appName"] = "HINT Control"

plugins {
    alias(libs.plugins.kotlin.native.cocoapods) apply false
    alias(libs.plugins.moko.resources) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.buildkonfig) apply false
    alias(libs.plugins.kotlin.atomicfu) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.conveyor) apply false
    alias(libs.plugins.bugsnag.gradle) apply false
    alias(libs.plugins.compose.hot.reload) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.ksp) apply false
}

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
        providers.exec {
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
        providers.exec {
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
        providers.exec {
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
