val androidCompileSdk by extra(34)
val androidTargetSdk by extra(34)
val androidMinSdk by extra(24)
val javaVersion by extra(JavaVersion.VERSION_21)

val appVersionCode by extra(45)
val appVersionName by extra("1.12.4")

val appGroup by extra("dev.zwander")
val appPackageName by extra("dev.zwander.arcadyankvd21control")

plugins {
    alias(libs.plugins.kotlin.native.cocoapods) apply false
    alias(libs.plugins.moko.resources) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.buildkonfig) apply false
    alias(libs.plugins.kotlin.atomicfu) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.conveyor) apply false
    alias(libs.plugins.bugsnag.android) apply false
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
