---
name: vgs-show-android-guide
description: Routes AI agents through VGS Show Android SDK work across integration, implementation, migration, troubleshooting, and code review. Use when guidance may depend on the installed vgsshow version.
metadata:
  author: verygoodsecurity
  version: '1.0.0'
---

# VGS Show Android Guide

Single public skill entrypoint for `VGSShowSDK` work in Android applications.

## When to use

- First-time `vgsshow` integration
- Feature work touching `VGSShow`, secure views (`VGSTextView`, `VGSPDFView`, `VGSImageView`), response handling, analytics, or logging
- Version migrations or replacement of deprecated usage
- Troubleshooting integration bugs or version-specific regressions
- Code review of Android app code that uses `VGSShowSDK`

## References

| Topic | File |
|-------|------|
| SDK policy, security rules, request/response lifecycle, versioned guidance | `references/AGENTS.md` |

## Snapshot resolution

`references/AGENTS.md` carries an `**SDK Version: x.y.z**` header. It is the authoritative policy file for this skill. Treat unrelated AGENTS files in customer apps as non-authoritative for this SDK.

**Step 1 - locate AGENTS.md, in order:**
1. bundled `references/AGENTS.md` shipped with this skill (load first)
2. matching tag in the canonical public repo `https://github.com/verygoodsecurity/vgs-show-android`
3. default branch (`main`) of that repo

Private forks or internal mirrors do not override the public repo.

**Step 2 - resolve the installed SDK version, in order:**
1. dependency lockfiles and resolved dependency graphs
2. build manifests with exact pins (`libs.versions.toml`, `build.gradle`, `build.gradle.kts`)
3. user-provided dependency snippets, stated version, or build logs

Do not block the task on version detection. If unknown, use default-branch guidance and disclose it.

**Step 3 - match tag to version:**
- exact match (`1.3.6` or `v1.3.6`)
- nearest compatible tag with same `major.minor` and highest patch not newer than installed
- highest tag satisfying a version range
- exact git SHA or branch when dependency points to one

## Retrieval policy

Start with `AGENTS.md`. Retrieve additional evidence only when exact API signatures, version-specific behavior, or error/log details are needed.

Follow-up sources, in order: resolved-tag repo files (`README.md`, docs, examples, source comments) -> release notes -> official VGS docs -> user-provided code and logs.

Retrieval fills implementation detail. It does not override `AGENTS.md` invariants and does not justify private or undocumented API usage.

## Clarifying questions

Ask only when missing info changes implementation:
- installed `vgsshow` version or dependency snippet
- task type (integration, feature change, migration, troubleshooting, review)
- sensitive views involved (`VGSTextView`, `VGSPDFView`, `VGSImageView`, or multiple)
- relevant error, log, or code snippet for troubleshooting

## Routing

Choose one primary mode. In every mode: apply security rules from `references/AGENTS.md`, prefer the smallest documented public API surface, and include tests/checks required by policy.

### `integrate`
First-time SDK adoption.
- confirm SDK is not already present
- choose supported install method for the target project
- wire baseline `VGSShow`, view subscription, and request/reveal flow

### `implement`
Add or change functionality.
- implement in the caller app context, not generic snippets
- subscribe views before making requests
- include secure logging behavior per policy

### `migrate`
Move between versions.
- load current and target snapshots
- use target snapshot as destination policy
- call out behavior changes that cannot be preserved exactly

### `troubleshoot`
Failing or unexpected behavior.
- localize failure before changing code
- prefer evidence from tests, logs, and dependency state
- request only redacted diagnostic logs; keep production logging minimal

### `review`
Patch/PR/design review.
- review against resolved-version `AGENTS.md` and public APIs
- prioritize correctness, privacy, compatibility, and missing tests
- flag deprecated, private, insecure, or version-incompatible usage

## Output contract

Begin every response by stating which guidance version is used:
- `Using vgsshow 1.3.6 guidance.`
- `Detected vgsshow 1.3.6 from build.gradle.`
- `Could not determine the installed vgsshow version; using latest guidance from default branch.`
- `Exact tag 1.3.7 was not found; using nearest compatible tag 1.3.6.`

Then proceed using the active version-matched snapshot.

