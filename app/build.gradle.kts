plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.detekt)
}

android {
    namespace = "com.verygoodsecurity.demoshow"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.verygoodsecurity.demoshow"
        minSdk = 23
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    implementation(project(":vgsshow"))


    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.verygoodsecurity.collect) {
        exclude(group = "com.verygoodsecurity", module = "vgs-sdk-analytics-android")
    }
    implementation(libs.verygoodsecurity.pdf.viewer)

    implementation(libs.activity.compose)
    implementation(libs.compose.material)
    implementation(libs.compose.ui.tooling.preview)

    debugImplementation(debugLibs.compose.ui.tooling)

    testImplementation(testLibs.junit)
    androidTestImplementation(androidTestLibs.androidx.junit.ext)
    androidTestImplementation(androidTestLibs.androidx.junit.ext.ktx)
    androidTestImplementation(androidTestLibs.androidx.runner)
    androidTestImplementation(androidTestLibs.androidx.rules)
    androidTestImplementation(androidTestLibs.androidx.uiautomator)
    androidTestImplementation(androidTestLibs.androidx.espresso.intents)
    androidTestImplementation(androidTestLibs.androidx.espresso.core)
    androidTestImplementation(androidTestLibs.hamcrest.integration)
    androidTestImplementation(androidTestLibs.espresso.contrib)
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
