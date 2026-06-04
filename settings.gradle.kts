pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("debugLibs") {
            from(files("gradle/debug-libs.versions.toml"))
        }
        create("testLibs") {
            from(files("gradle/test-libs.versions.toml"))
        }
        create("androidTestLibs") {
            from(files("gradle/android-test-libs.versions.toml"))
        }
    }
}

rootProject.name = "vgs-show-android"
include(":app", ":vgsshow", ":vgs-sdk-analytics:VGSClientSDKAnalytics")

