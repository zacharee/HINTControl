import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.reload.ComposeHotRun
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag

plugins {
    alias(libs.plugins.kotlin.atomicfu)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.conveyor)
    alias(libs.plugins.compose.hot.reload)
}

val appVersionName: String by rootProject.extra
val appPackageName: String by rootProject.extra
val appGroup: String by rootProject.extra

val javaVersion: JavaVersion by rootProject.extra

group = appGroup
version = appVersionName

kotlin {
    jvmToolchain {
        this.languageVersion.set(JavaLanguageVersion.of(javaVersion.toString().toInt()))
        this.vendor.set(JvmVendorSpec.MICROSOFT)
    }

    jvm()

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":common"))
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        val pkg = appPackageName

        nativeDistributions {
            includeAllModules = true

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
            packageVersion = appVersionName

            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Exe)
        }
    }
}

tasks.named<hydraulic.conveyor.gradle.WriteConveyorConfigTask>("writeConveyorConfig") {
    dependsOn(tasks.named("build"))

    doLast {
        val config = StringBuilder()
        config.appendLine("app.fsname = hintcontrol")
        config.appendLine("app.display-name = ${project.rootProject.extra["appName"]}")
        config.appendLine("app.rdns-name = $appPackageName")
        destination.get().asFile.appendText(config.toString())
    }
}

dependencies {
    // Use the configurations created by the Conveyor plugin to tell Gradle/Conveyor where to find the artifacts for each platform.
    linuxAmd64(compose.desktop.linux_x64)
    linuxAarch64(compose.desktop.linux_arm64)
    macAmd64(compose.desktop.macos_x64)
    macAarch64(compose.desktop.macos_arm64)
    windowsAarch64(compose.desktop.windows_arm64)
    windowsAmd64(compose.desktop.windows_x64)
}

// region Work around temporary Compose bugs.
configurations.all {
    attributes {
        // https://github.com/JetBrains/compose-jb/issues/1404#issuecomment-1146894731
        attribute(Attribute.of("ui", String::class.java), "awt")
    }
}

tasks.withType<ComposeHotRun>().configureEach {
    mainClass.set("MainKt")
}
