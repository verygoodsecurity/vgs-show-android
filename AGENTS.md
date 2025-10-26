# AGENTS.md

This file provides **context, instructions, and precise commands** for AI agents working with the VGS Show Android SDK project.

## Project Context (Project Overview)

| Attribute | Value |
| :--- | :--- |
| **Name** | VGS Show Android SDK |
| **Purpose** | A library for securely revealing (displaying) sensitive data to users without the data passing through the client's systems. |
| **Core Technology** | Android (Kotlin/Java), Gradle |
| **License** | MIT |

## Project Dependencies

Agents should ensure the project meets these minimum requirements:

| Dependency | Version |
| :--- | :--- |
| **Min SDK** | 21 |
| **Target SDK** | 34 |
| **Kotlin Plugin** | 1.9.22 |
| **OkHttp** | 4.12.0 |

## Project Structure

The main modules relevant to an agent are:
* **VGSShow SDK**: The core API for secure data revealing.
* **app**: Sample application used for development and testing.

## Development Environment Setup

The necessary prerequisites for working with the project are:
* **Android Studio**
* **JDK 8+**

## Build and Testing Commands

As this is an Android/Gradle project, all commands should be executed via the **Gradle Wrapper** (`./gradlew`) from the root directory.

| Purpose | Command |
| :--- | :--- |
| **Run All Unit Tests** | `./gradlew test` |
| **Full Project Build** | `./gradlew build` |
| **Install Debug App** | `./gradlew :app:installDebug` |

## Dependency Management

The VGS Show SDK dependency must be added to the appropriate **`build.gradle`** file:

```gradle
dependencies {
    // Core VGS Show dependency
    implementation "com.verygoodsecurity:vgsshow:<version>"
    
    // Standard AndroidX dependencies
    implementation "androidx.appcompat:appcompat:<version>"
    implementation "com.google.android.material:material:<version>"
}
```

## Releases
To follow **VGS Show SDK** updates and changes check the [releases](https://github.com/verygoodsecurity/vgs-show-android/releases) page.