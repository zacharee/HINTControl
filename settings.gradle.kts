pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://maven.hq.hydraulic.software")
    }
}

rootProject.name = "ArcadyanKVD21Control"

include(":android", ":desktop", ":common")
