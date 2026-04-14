# vgs-show-android-private Dependency Map

## Project Structure

Android SDK library (Gradle/Kotlin) for securely revealing sensitive data via VGS.
Multi-module project with Gradle version catalogs (TOML-based).

| Module | Role |
|--------|------|
| `vgsshow` | Core SDK library — published to Maven Central as `com.verygoodsecurity:vgsshow` |
| `app` | Demo/sample application for integration testing and showcasing SDK features |
| `vgs-sdk-analytics:VGSClientSDKAnalytics` | Analytics submodule (git submodule) — tracks SDK usage telemetry |

Key ecosystems: **Gradle (Kotlin/Android)**, with Jetpack Compose UI layer and OkHttp networking.

## Dependency Categories

### Always Low Risk (Auto-merge Candidates)

| Pattern | Example | Reason |
|---------|---------|--------|
| Test-only dependencies | `junit:junit`, `io.mockk:mockk`, `org.robolectric:robolectric`, `org.json:json` | Test scope only — no runtime impact |
| Android test dependencies | `androidx.test:runner`, `androidx.test:rules`, `androidx.test.ext:junit`, `androidx.test.espresso:*`, `androidx.test.uiautomator:*`, `org.hamcrest:hamcrest-integration` | androidTest scope only |
| Debug-only dependencies | `androidx.compose.ui:ui-tooling` | debugImplementation — stripped from release builds |
| Build/lint plugins | `io.gitlab.arturbosch.detekt` | Static analysis, build-time only |
| Documentation plugins | `org.jetbrains.dokka` | Doc generation, no runtime impact |
| Code coverage plugins | `org.jetbrains.kotlinx.kover` | Coverage reporting, no runtime impact |
| Publishing plugins | `com.vanniktech.maven.publish` | Release tooling, no runtime impact |

### Needs Quick Review

| Pattern | Example | Reason | Expected Test Coverage |
|---------|---------|--------|----------------------|
| AndroidX UI libraries | `androidx.appcompat:appcompat`, `androidx.core:core-ktx`, `androidx.constraintlayout:constraintlayout` | Runtime but stable API, backward-compatible minor bumps | Demo app exercises UI components |
| Compose UI libraries | `androidx.compose.material:material`, `androidx.compose.ui:ui-tooling-preview`, `androidx.activity:activity-compose` | Runtime UI layer — minor bumps are usually safe | Demo app Compose screens |
| Ktor client libraries | `io.ktor:ktor-client-core`, `io.ktor:ktor-client-okhttp`, `io.ktor:ktor-client-darwin` | Networking layer used in KMP module — minor bumps usually backward-compatible | Unit tests cover HTTP interactions |
| Kotlin coroutines test | `org.jetbrains.kotlinx:kotlinx-coroutines-test` | Test utility but tied to coroutines runtime version | Test suite uses this directly |
| Mokkery plugin | `dev.mokkery` | KMP mocking framework plugin — test tooling but version must align with Kotlin | Test compilation validates compatibility |

### Needs Deep Review

| Pattern | Example | Reason | Expected Test Coverage |
|---------|---------|--------|----------------------|
| Kotlin compiler/stdlib | `org.jetbrains.kotlin.android`, `org.jetbrains.kotlin.plugin.compose`, `org.jetbrains.kotlin.multiplatform`, `org.jetbrains.kotlin.plugin.serialization` | Core language — major/minor bumps can break compilation or change behavior | Full build + test suite must pass |
| Android Gradle Plugin | `com.android.application`, `com.android.library` (AGP) | Build system — affects compilation, packaging, minification | Full build validates; watch for min SDK / target SDK changes |
| OkHttp | `com.squareup.okhttp3:okhttp` | Core networking dependency exposed as `api` — breaking changes affect all consumers | Unit tests + integration tests cover HTTP layer |
| VGS internal SDKs | `com.verygoodsecurity:vgscollect`, `com.verygoodsecurity:android-pdf-viewer`, `com.verygoodsecurity:vgs-sdk-analytics-android` | Internal dependencies — version bumps may carry undocumented breaking changes | Demo app integration tests; analytics submodule tests |

## Historical Patterns (from PR analysis)

6 open Renovate PRs as of 2026-04-14:
- Kotlin monorepo update (v2.3.20) — deep review needed (compiler + all Kotlin plugins)
- VGS SDK analytics update (v1.0.4) — internal dependency, needs review
- Ktor monorepo update (v3.4.2) — quick review (networking minor bump)
- Dokka update (v2.2.0) — low risk (doc tooling)
- Kover update (v0.9.8) — low risk (coverage tooling)
- Pin dependencies — needs review (initial pinning establishes baselines)

## Renovate Configuration

Minimal configuration — `renovate.json` contains only the schema reference with no
`extends`, labels, grouping, automerge rules, or package rules. This means Renovate
is using its default preset (`config:recommended`). Dependencies are not grouped
beyond Renovate's default monorepo grouping. No automerge is configured. The
`renovate` label seen on PRs likely comes from the org-level Renovate config or
defaults.
