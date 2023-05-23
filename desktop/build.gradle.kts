@file:Suppress("UNUSED_VARIABLE")

import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("dev.hydraulic.conveyor") version "1.6"
}

group = "dev.zwander"
version = rootProject.extra["app_version_code"].toString()

kotlin {
    jvm {
        jvmToolchain(rootProject.extra["java_version"].toString().toInt())
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":common"))
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        val pkg = rootProject.extra["package_name"].toString()

        nativeDistributions {
            windows {
                menu = true
//                console = true
                iconFile.set(project.file("src/jvmMain/resources/icon.ico"))
                targetFormats(TargetFormat.Exe, TargetFormat.AppImage)
            }

            macOS {
                bundleID = pkg
                iconFile.set(project.file("src/jvmMain/resources/icon.icns"))
                targetFormats(TargetFormat.Dmg)
            }

            linux {
                iconFile.set(project.file("src/jvmMain/resources/icon.png"))
                targetFormats(TargetFormat.Deb, TargetFormat.AppImage)
            }

            packageName = pkg
            packageVersion = rootProject.extra["app_version_name"].toString()

            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Exe)
        }
    }
}

dependencies {
    // Use the configurations created by the Conveyor plugin to tell Gradle/Conveyor where to find the artifacts for each platform.
    linuxAmd64(compose.desktop.linux_x64)
    macAmd64(compose.desktop.macos_x64)
    macAarch64(compose.desktop.macos_arm64)
    windowsAmd64(compose.desktop.windows_x64)
}

// region Work around temporary Compose bugs.
configurations.all {
    attributes {
        // https://github.com/JetBrains/compose-jb/issues/1404#issuecomment-1146894731
        attribute(Attribute.of("ui", String::class.java), "awt")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(rootProject.extra["java_version"].toString()))
    }
}
