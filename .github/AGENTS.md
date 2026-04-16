# AGENTS.md

This file is a repository-specific addendum for AI coding agents working in this monorepo.
Read `/AGENTS.md` in full first and treat it as the baseline policy for VGS Show integration behavior.

## Scope
- Use `/AGENTS.md` for SDK integration rules and security policy.
- Use this file for repository architecture, build/test workflow, and module-level conventions.
- If guidance conflicts, follow `/AGENTS.md` for SDK behavior and this file for repo mechanics.
- When changing SDK integration behavior, public API surface, or integration examples, update `/AGENTS.md` in the same change so downstream integration agents stay aligned.
- Use SKILLS/specialized agents when present and relevant; fall back to direct implementation only when no applicable SKILL is available.

## 1. Core Concepts (Mental Model)
- This repository is the **VGS Show Android SDK** (`vgsshow/`) plus a sample host app (`app/`).
- Main shipping Android SDK module: `vgsshow/`.
- Sample host app for manual/instrumentation validation: `app/`.
- Shared analytics implementation lives in `vgs-sdk-analytics/VGSClientSDKAnalytics` (KMP), consumed by build type in `vgsshow/build.gradle`.

### Project Mental Model
- **Primary flow**: VGS Vault holds sensitive data → `VGSShow` makes a request to the VGS proxy → revealed data is rendered inside secure views (`VGSTextView`, `VGSPDFView`, `VGSImageView`) → app never receives raw sensitive values.
- **Change surface map**:
  - SDK orchestration, request/response logic, view subscription: `vgsshow/src/main/`.
  - Secure widget behavior (`VGSTextView`, `VGSPDFView`, `VGSImageView`): `vgsshow/src/main/java/com/verygoodsecurity/vgsshow/widget/`.
  - Network layer, HTTP client, headers: `vgsshow/src/main/java/com/verygoodsecurity/vgsshow/core/network/`.
  - Manual verification / demo flows: `app/src/main/`.
- **Testing map**:
  - Fast regression tests for SDK behavior: `vgsshow/src/test/` (Robolectric / JUnit / MockK stack from `gradle/test-libs.versions.toml`).
  - Device / instrumentation checks for sample integrations: `app/src/androidTest/`.
- **Release-facing boundaries**:
  - Published Android artifact work centers on `vgsshow/`.
  - Shared analytics implementation is versioned in `vgs-sdk-analytics/VGSClientSDKAnalytics` and linked into `vgsshow` by build type.

## 1A. Environment Preconditions
- Java toolchain is pinned to JVM 1.8 for both `vgsshow/` and `app/` (`JavaVersion.VERSION_1_8`).
- Android SDK levels are currently `compileSdkVersion 36`, `targetSdkVersion 36`, `minSdkVersion 21` for `vgsshow/`; `minSdkVersion 23` for `app/`.
- Demo app runtime config is injected from `local.properties` via `vgsshow/build.gradle` (`VGS_VAULT_ID`).
- Never hard-code vault IDs or credentials in source; always read from `local.properties` or equivalent secure config.

## 2. Public Building Blocks (Summary Table)
- Module boundaries are defined in `settings.gradle`:
  - `:app`
  - `:vgsshow`
  - `:vgs-sdk-analytics:VGSClientSDKAnalytics`
- Dependency versions are centralized in `gradle/libs.versions.toml`; prefer version-catalog updates over inline versions.
- Test/debug-only catalogs are split into `gradle/debug-libs.versions.toml`, `gradle/test-libs.versions.toml`, `gradle/android-test-libs.versions.toml`.

### Core Project Architecture and Classes
- **SDK orchestration/API (`vgsshow/src/main/java/com/verygoodsecurity/vgsshow/`)**:
  - `VGSShow` – main entry point; manages vault connection, view subscriptions, and request dispatch.
  - `VGSEnvironment` – environment enum (`SANDBOX`, `LIVE`, and custom data-region variants).
  - `VGSHttpMethod` – HTTP method enum used when building requests.
  - `VGSRequest` / `VGSResponse` – request configuration and response models (`core/network/model/`).
  - `VGSOnResponseListener` – callback interface for request results (`core/listener/`).
- **Secure view widgets (`vgsshow/src/main/java/com/verygoodsecurity/vgsshow/widget/`)**:
  - `VGSTextView` – securely displays revealed textual data; supports transformation/redaction.
  - `VGSPDFView` – securely displays revealed PDF files (requires consumer to add the PDF-viewer dependency).
  - `VGSImageView` – securely displays revealed image data.
  - All widgets extend `VGSView` (`widget/core/VGSView.kt`).
- **Network layer (`vgsshow/src/main/java/com/verygoodsecurity/vgsshow/core/network/`)**:
  - `HttpRequestManager` / `IHttpRequestManager` – executes proxy requests.
  - `VGSHttpBodyFormat` – body format enum (JSON, URL-encoded).
  - `ProxyStaticHeadersStore` – manages static request headers.
  - OkHttp-based client with CNAME interceptor support.
- **Analytics & logging**:
  - `VGSAnalyticsClient` (via `VGSSharedAnalyticsManager`) – toggle analytics with `vgsShow.setAnalyticsEnabled(false)`.
  - `VGSShowLogger` – control log verbosity; keep at `.NONE` in production.
- **Sample app flows (`app/src/main/java/com/verygoodsecurity/demoshow/`)**:
  - Entry / navigation: `ui/MainActivity.kt`.
  - Collect + Show demos: `ui/activity/CollectAndShowActivity.kt`, `ui/activity/CollectAndShowFragmentActivity.kt`, `ui/activity/CollectAndShowViewPagerActivity.kt`.
  - PDF reveal demo: `ui/activity/PDFActivity.kt`.
  - Image reveal demo: `ui/activity/ImageActivity.kt`.
  - Compose demo: `ui/activity/ComposeActivity.kt`.
  - Apply-response demo (multi-fragment): `ui/activity/apply_response_demo/`.

## 4. View Setup & Configuration Pattern
- For real integration behavior and secure view usage patterns, use `/AGENTS.md` section 4 as source of truth.
- In this repo, the sample wiring patterns are primarily under `app/src/main/` and should be used as runnable examples when validating behavior changes.
- `VGSImageView` follows the same `subscribe`/`contentPath` pattern as `VGSTextView` and `VGSPDFView`.

## 6. Submission APIs
- Keep submission API usage aligned with `/AGENTS.md`.
- For SDK changes, validate with unit tests in `vgsshow/src/test/` before checking sample app behavior.
- Key test files:
  - `VGSShowTest.kt` – end-to-end SDK behavior.
  - `VGSTextViewTest.kt`, `VGSPDFViewTest.kt`, `VGSImageViewTest.kt` – per-widget behavior.
  - `HttpRequestManagerTest.kt` – network layer regression.

## 9. Analytics & Privacy
- `vgsshow` uses the local analytics module in debug and the published artifact in release:
  - `debugImplementation project(':vgs-sdk-analytics:VGSClientSDKAnalytics')`
  - `releaseImplementation(libs.vgs.sdk.analytics.android)`
- Preserve this split unless intentionally changing analytics integration strategy.

## 10. Logging & Redaction Policy
- Follow `/AGENTS.md` redaction rules.
- CI runs include static analysis and test publishing; do not add logs that expose raw field values because reports/artifacts are uploaded.
- `VGSShowLogger` must be set to `.NONE` (or equivalent minimal level) in production builds.

## 11. Final Rule for Agents
- Prefer the narrowest module change that satisfies the task (e.g., `vgsshow/` only; avoid sample app edits unless needed for validation/demo).
- If updating dependency or SDK behavior, run the same core checks used in CI:
  - `./gradlew vgsshow:detekt --continue`
  - `./gradlew vgsshow:testDebugUnitTest --continue`
- For UI-test-related changes (release branches), CI assembles and runs instrumentation:
  - `./gradlew app:assembleDebug app:assembleAndroidTest`

End of .github/AGENTS.md
