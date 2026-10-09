# AGENTS.md

**SDK Version: 1.3.6**

This guide is tailored for autonomous engineering agents integrating `VGSShowSDK` into Android applications. It focuses on deterministic, automatable steps for securely displaying sensitive data.

Documentation Source of Truth Across Versions
- The canonical durable instruction source for this repository lives at `skills/vgs-show-android-guide/references/AGENTS.md`.
- The single public AI skill entrypoint lives at `skills/vgs-show-android-guide/SKILL.md`. It routes task type and version selection, then defers SDK rules and invariants to this file.
- The installed skill bundle must ship `skills/vgs-show-android-guide/references/AGENTS.md` so standalone skill installs receive this policy file automatically.
- For public skill and documentation workflows, the canonical repository for tags and versioned docs is `https://github.com/verygoodsecurity/vgs-show-android`.
- When the SDK version used by the target project can be determined, agents MUST prefer the durable guidance snapshot from the matching Git tag in the public `verygoodsecurity/vgs-show-android` repository before giving version-sensitive guidance. For older tags, that snapshot may still live at repo-root `AGENTS.md`.
- If an exact tag is unavailable, agents MUST use the nearest compatible tag and clearly disclose the mismatch before giving version-sensitive guidance.
- Private forks or internal mirrors do not override the public repository as the source of truth for public skill guidance.
- If the SDK is not installed or its version cannot be determined, agents SHOULD use the default-branch copy of this file and disclose that latest guidance is used.

## Scope
- Use this file as the ONLY high-level instruction source when adding, updating, or testing VGS Show Android SDK (`vgsshow`) usage in a downstream app.
- Contain ONLY public, non-deprecated APIs.
- Emphasis: security, correctness, determinism, reproducibility.

## Success Criteria for Any Agent Task
1. Sensitive data NEVER logged, persisted, or leaked to analytics.
2. All network requests are made to the correct VGS Vault and environment.
3. The `VGSShow` instance is properly destroyed to avoid memory leaks.
4. APIs used exist in current public surface and are not deprecated.
5. Version pins updated intentionally with a migration note and tests green.

---
## 1. Core Concepts (Mental Model)
- `VGSShow` orchestrates the secure display of sensitive data from a VGS Vault you own (identified by vault ID + environment / data region).
- UI views (`VGSTextView`, `VGSPDFView`, `VGSImageView`) are subscribed to a `VGSShow` instance and are populated with data from the response of a request made by the `VGSShow` instance.
- You interact with the revealed data through the secure views.
- The app layer never receives raw sensitive values.

---
## 1A. Environment Preconditions
Purpose: ensure correct vault/environment pairing before any request.

Required:
- Non-empty `vaultId` string (assert with `precondition(!vaultId.isEmpty)` in debug builds).
- Explicit environment selection: `Environment.SANDBOX` for development/testing; `Environment.LIVE` for production only.
- Keep sandbox and live vault IDs distinct; never point `.LIVE` at a sandbox vault ID.
- Optional hostname override (if provided): verify matches VGS dashboard configuration before deploy.

Quick Examples:
```kotlin
// Sandbox VGSShow (dev/test)
val vgsShow = VGSShow(context, sandboxVaultId, Environment.SANDBOX)
// Production VGSShow (release build)
val liveVgsShow = VGSShow(context, liveVaultId, Environment.LIVE)
```
Failure Modes:
- Empty vaultId -> initialization should fail fast (use preconditions).
- Using `.LIVE` in debug with test data unintentionally -> enforce build configuration checks.

Security Note: Never derive environment from user input; it must be a static config determined at build or app launch.

---
## 2. Public Building Blocks (Summary Table)
(Do NOT assume additional behavior outside listed intents.)
- `VGSShow`: orchestrates requests and subscribes secure views.
- `VGSTextView`: securely displays revealed textual data; supports transformation/redaction.
- `VGSPDFView`: securely displays revealed PDF files.
- `VGSImageView`: securely displays revealed image data.
- `VgsText`, `VgsImage`, `VgsPdf`: native Jetpack Compose equivalents of the views above, driven by `rememberVgsTextState` / `rememberVgsImageState` / `rememberVgsPdfState` (see section 4A). `VgsText` exists in `...widget.compose.material` and `...widget.compose.material3`.
- `VGSOnResponseListener`: for handling responses from the VGS proxy.
- `VGSRequest`: for configuring requests to the VGS proxy.
- `VGSResponse`: for representing responses from the VGS proxy (`VGSResponse.Success`, `VGSResponse.Error`).
- `VGSAnalyticsClient`: toggle analytics collection.
- `VGSShowLogger`: adjust log verbosity (ensure `.NONE` in production if required).

---
## 4. View Setup & Configuration Pattern
Canonical view snippet (XML Layout):
```xml
<com.verygoodsecurity.vgsshow.widget.VGSTextView
    android:id="@+id/textView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:contentPath="json.my_data" />

<com.verygoodsecurity.vgsshow.widget.VGSPDFView
    android:id="@+id/pdfView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:contentPath="json.my_pdf" />

<com.verygoodsecurity.vgsshow.widget.VGSImageView
    android:id="@+id/imageView"
    android:layout_width="match_parent"
    android:layout_height="200dp"
    app:contentPath="json.my_image" />
```

Binding in Activity/Fragment:
```kotlin
val vgsShow = VGSShow(this, "YOUR_VAULT_ID", Environment.SANDBOX)
vgsShow.subscribe(findViewById(R.id.textView))
vgsShow.subscribe(findViewById(R.id.pdfView))
vgsShow.subscribe(findViewById(R.id.imageView))
```

---
## 4A. Jetpack Compose Widgets
Use these instead of the XML views in Compose UI. Do NOT wrap the XML views in `AndroidView`; the `VGSTextViewWrapper` / `VGSImageViewWrapper` / `VGSPDFViewWrapper` helpers are deprecated.

Pattern: create state with `rememberVgs…State`, pass `state.value` to the composable. The state subscribes to and unsubscribes from `VGSShow` automatically; never call `subscribe` / `unsubscribe` yourself for these.
```kotlin
val textState = rememberVgsTextState(
    show = vgsShow,
    contentPath = "json.card_number",
    visualTransformation = VgsChainedVisualTransformation(
        listOf(
            VgsRegexVisualTransformation(Regex("(\\d{4})(?=\\d)"), "$1 "),
            VgsSecureVisualTransformation(VGSTextRange(0, 11)),
        )
    ),
)
VgsText(state = textState.value) // com.verygoodsecurity.vgsshow.widget.compose.material (or .material3)

val imageState = rememberVgsImageState(vgsShow, "json.my_image", onLoadError = { /* ... */ })
if (imageState.value.hasImage) VgsImage(state = imageState.value, contentDescription = "ID scan")

val pdfState = rememberVgsPdfState(vgsShow, "json.my_pdf", onRenderError = { /* ... */ })
if (pdfState.value.hasDocument) VgsPdf(state = pdfState.value, modifier = Modifier.fillMaxSize())
```
Rules:
- Revealed data is never exposed. States have no public getter for text, bitmap or PDF bytes. Do not try to read it back (`onTextLayout` and `inlineContent` are intentionally unavailable on `VgsText`). The only exit paths are `copyRawToClipboard` / `copyFormattedToClipboard` on `VgsTextState`.
- Masking and formatting are display-only: pass a `VgsVisualTransformation` (`VgsSecureVisualTransformation`, `VgsRegexVisualTransformation`, `VgsChainedVisualTransformation`) to `rememberVgsTextState`.
- Branch on state flags for loading/empty UI: `VgsTextState.isEmpty`, `VgsImageState.hasImage` / `isImageValid`, `VgsPdfState.hasDocument`. There is no `placeholder` parameter.
- Callbacks go on the `remember…` function: `onLoadComplete` / `onLoadError` (image), `onRenderStart` / `onRenderComplete` / `onRenderError` / `onPageChanged` (PDF). PDF callbacks only fire while `VgsPdf` is in composition.
- Revealed text, image and PDF are never saved across configuration change or process death (sensitive); call `request` again to reveal after recreation. `clearText()` / `clearImage()` / `clearDocument()` on the `MutableState` clear revealed content.
- `VgsTextState.copyFormattedToClipboard` copies the formatted value without masking, same as the legacy `VGSTextView`.
- `VgsText` needs Material (`androidx.compose.material`) or Material 3 on the app classpath; `VgsPdf` needs the `android-pdf-viewer` dependency.
- Use `ignore = true` to exclude a field from reveal requests without unsubscribing.

---
## 5. Observing & Using View State
State listening:
```kotlin
vgsShow.addOnResponseListener(object : VGSOnResponseListener {
    override fun onResponse(response: VGSResponse) {
        when(response) {
            is VGSResponse.Success -> {} // Handle success
            is VGSResponse.Error -> {}   // Handle error
        }
    }
})
```
Never log raw revealed content or sensitive field values.

---
## 6. Submission APIs
Listener Variant:
```kotlin
vgsShow.addOnResponseListener(object : VGSOnResponseListener {
    override fun onResponse(response: VGSResponse?) {
        // Handle response
    }
})
vgsShow.requestAsync("/post", VGSHttpMethod.POST)
```
Async/Await (using coroutines):
```kotlin
lifecycleScope.launch {
    val response = vgsShow.request("/post", VGSHttpMethod.POST)
    // Handle response
}
```

---
## 9. Analytics & Privacy
Opt out if mandated:
```kotlin
vgsShow.setAnalyticsEnabled(false)
```
Do not modify analytics payload structure. Toggle only.

---
## 10. Logging & Redaction Policy
Allowed: error categories (e.g. timeout, validationFailure).
Forbidden: unredacted revealed content, vault ID in user-visible error messages.
Ensure production logger level minimal (`.NONE` or equivalent).

---
## 11. Final Rule for Agents
If uncertain between two approaches, choose the one that:
1. Uses fewer APIs.
2. Avoids exposing raw sensitive data.
3. Prefers non-deprecated, documented public surface.

Escalate (via comments / PR note) only when a required behavior is impossible without private or deprecated API access.

End of AGENTS.md

