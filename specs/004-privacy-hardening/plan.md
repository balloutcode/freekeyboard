# Implementation Plan: 004 Privacy & code hardening

**Branch**: `main` (landed 2026-11-08) | **Spec**: [spec.md](./spec.md)

## Summary

Close security-review and Bugbot findings without new product surfaces: Manifest backup gates, clipboard capture/store policy, password-field input guards, caret/delete/Enter correctness, Features chrome on suppressed editors, release R8.

## Technical Context

Unchanged stack from 001–003. Touches:

| Area | Files |
|------|--------|
| Backup | `AndroidManifest.xml`, `res/xml/backup_rules.xml`, `data_extraction_rules.xml` |
| Clipboard | `ClipboardHistoryStore`, `ClipboardSensitivity`, `FreekeyboardImeService`, `ClipboardPanel`, Settings |
| Input | `InputConnectionFacade`, `EditorInfoInterpreter`, `KeyboardController` |
| UI | `KeyboardRoot` (toolbar on suppress), `KeyGrid` (a11y long-press) |
| Release | `app/build.gradle.kts` minify/shrink, `proguard-rules.pro` |

## Constitution Check

| Principle | Status |
|-----------|--------|
| I FOSS | PASS |
| II Privacy | PASS — backup off, clipboard opt-in, password guards |
| III UX parity | PASS — toolbar on suppress; caret swipe still works |
| VI Toolchain | PASS — R8 on release |
| VII Spec-driven | PASS — this feature dir |

## Complexity Tracking

Deferred: EncryptedDataStore for clipboard (needs Keystore UX + migration). Current mitigations (backup off + TTL + opt-in) are sufficient for F-Droid trust bar.
