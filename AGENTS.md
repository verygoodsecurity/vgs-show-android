# AGENTS.md

This guide is tailored for autonomous engineering agents integrating `VGSShowSDK` into Android applications. It focuses on deterministic, automatable steps for securely displaying sensitive data.

## Scope
- Use this file as the ONLY high-level instruction source when adding, updating, or testing VGS Show Android SDK (`vgsshow`) usage in a downstream app.
- Contain ONLY public, non-deprecated APIs.
- Emphasis: security, correctness, determinism, reproducibility.

## Success Criteria for Any Agent Task
1. Sensitive data NEVER logged, persisted, or leaked to analytics.
2. All network requests are made to the correct VGS Vault and environment.
3. The `VGSShow` instance is properly destroyed to avoid memory leaks.

---
## 1. Core Concepts (Mental Model)
- `VGSShow` orchestrates the secure display of sensitive data from a VGS Vault you own (identified by vault ID + environment / data region).
- UI views (`VGSTextView`, `VGSPDFView`) are subscribed to a `VGSShow` instance and are populated with data from the response of a request made by the `VGSShow` instance.
- You interact with the revealed data through the secure views.

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
- Empty vaultId → initialization should fail fast (use preconditions).
- Using `.LIVE` in debug with test data unintentionally → enforce build configuration checks.

Security Note: Never derive environment from user input; it must be a static config determined at build or app launch.

---
## 2. Public Building Blocks (Summary Table)
(Do NOT assume additional behavior outside listed intents.)
- `VGSShow`: orchestrates requests and subscribes secure views.
- `VGSTextView`: securely displays revealed textual data.
- `VGSPDFView`: securely displays revealed PDF files.
- `VGSOnResponseListener`: for handling responses from the VGS proxy.
- `VGSRequest`: for configuring requests to the VGS proxy.
- `VGSResponse`: for representing responses from the VGS proxy.
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
```

Binding in Activity/Fragment:
```kotlin
val vgsShow = VGSShow(this, "YOUR_VAULT_ID", Environment.SANDBOX)
vgsShow.subscribe(findViewById(R.id.textView))
vgsShow.subscribe(findViewById(R.id.pdfView))
```

---
## 5. Observing & Using View State
State listening:
```kotlin
vgsShow.addOnResponseListener(object : VGSOnResponseListener {
    override fun onResponse(response: VGSResponse) {
        // Handle response
        when(response) {
            is VGSResponse.Success -> {}// Handle success
            is VGSResponse.Error -> {}// Handle error
        }
    }
})
```
Never log raw file contents.

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

### Developer Prompt (Inline Example for a Single Task):

**Task:** Add a `VGSTextView` to display a credit card number with a custom redaction format.
Follow AGENTS.md Section 4.

Do not break existing secure views; add tests for redaction and display.

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
Forbidden: unredacted file content, vault ID in user-visible error messages.
Ensure production logger level minimal (`.NONE` or equivalent).

---
## 11. Final Rule for Agents
If uncertain between two approaches, choose the one that:
1. Uses fewer APIs.
2. Avoids exposing raw sensitive data.
3. Prefers non-deprecated, documented public surface.

Escalate (via comments / PR note) only when a required behavior is impossible without private or deprecated API access.

End of AGENTS.md