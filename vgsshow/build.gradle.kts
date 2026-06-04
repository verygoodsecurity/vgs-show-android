plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.dokka)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kover)
}

apply(from = "$rootDir/gradle/utils.gradle.kts")

val localProperty: (String) -> String by extra

android {
    namespace = "com.verygoodsecurity.vgsshow"
    compileSdk = 37

    defaultConfig {
        minSdk = 23

        buildConfigField("String", "VERSION_NAME", "\"${project.properties["VERSION_NAME"]}\"")
        buildConfigField("String", "VAULT_ID", "\"${localProperty("VGS_VAULT_ID")}\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    testOptions {
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    debugImplementation(project(":vgs-sdk-analytics:VGSClientSDKAnalytics"))
    releaseImplementation(libs.vgs.sdk.analytics.android)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)

    implementation(libs.compose.material)

    // Http
    api(libs.okhttp)

    // PDF viewer.
    // NOTE: Used by VGSPDFView. Consumer should add this dependency by itself as it has big size.
    compileOnly(libs.verygoodsecurity.pdf.viewer)

    testImplementation(testLibs.junit)
    testImplementation(testLibs.json)
    testImplementation(testLibs.robolectric)
    testImplementation(testLibs.mockk)
    testImplementation(testLibs.mockk)
    testImplementation(libs.verygoodsecurity.pdf.viewer)
}

kotlin {
    jvmToolchain(17)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

detekt {
    buildUponDefaultConfig = false
    allRules = false
    config.setFrom(files("$rootDir/.detekt/config.yml"))
}

dokka {
    dokkaPublications.html {
        outputDirectory.set(rootProject.file("docs"))
    }
}

tasks.withType<Javadoc>().configureEach {
    isEnabled = false
}

// Temporary workaround: AGP javadoc generation crashes on newer dependency bytecode metadata.
tasks.matching { it.name == "javaDocReleaseGeneration" }.configureEach {
    enabled = false
}
