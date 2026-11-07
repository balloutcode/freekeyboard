# Feature Specification: 004 Privacy & code hardening

**Feature Branch**: `004-privacy-hardening`  
**Created**: 2026-11-08  
**Status**: Implemented  
**Input**: Security review (12 findings) + Bugbot review (12 findings) closed in-tree

## Summary

Harden freekeyboard for IME trust: backup/clipboard/password-field privacy, plus correctness fixes for caret swipe, Features toolbar on suppressed fields, multiline Enter, and related logic bugs. No new network surface; no AutofillService.

## Clarifications (2026-11-08)

- **Backup**: `android:allowBackup="false"` plus DataStore exclusion rules for cloud backup / device transfer.
- **Clipboard**: opt-in (default off); capture only while keyboard visible; skip `EXTRA_IS_SENSITIVE`, password / suppressed / incognito; wipe on disable; pin TTL 7d; UUID ids; per-entry delete UI.
- **Password fields**: no autocorrect / double-space mutation; Features toolbar still reachable (word chips stay suppressed); clipboard panel blocked.
- **Caret swipe**: truncated text buffers MUST NOT drive absolute `setSelection` (DPAD fallback); password path never buffers large field text.
- **Multiline Enter**: `TYPE_TEXT_FLAG_MULTI_LINE` + NONE/UNSPECIFIED → insert `\n`, not DONE.
- **Release**: R8 minify + shrinkResources enabled with IME/settings keep rules.

## User Scenarios

### US1 — Clipboard opt-in (P0)

User leaves clipboard history off by default; enabling it stores local copies only while typing; disabling clears history.

### US2 — Password safety (P0)

In a password field, space never rewrites the secret; Features menu remains available; clipboard history is not offered.

### US3 — Long document caret (P1)

In a long note (>512 chars before caret), spacebar swipe moves the caret locally (no jump to document start).

## Requirements

| ID | Requirement |
|----|-------------|
| FR-001 | Application backup disabled; sensitive DataStore paths excluded from extraction rules. |
| FR-002 | Clipboard history default off; clearAll when toggled off. |
| FR-003 | Clipboard listener registered only while input view shown. |
| FR-004 | Skip sensitive clips and password/suppressed/incognito contexts. |
| FR-005 | Pinned clipboard TTL ≤ 7 days; unpinned ≤ 1 hour; prune persisted to disk. |
| FR-006 | `onSpace` skips autocorrect/period when `suppressSuggestions` / password. |
| FR-007 | Features toolbar-only strip when suggestions suppressed (incl. number/phone pads). |
| FR-008 | Truncated caret-swipe buffers use DPAD; `deleteBeforeCursor` UTF-16-safe fallback. |
| FR-009 | Multiline unspecified Enter inserts newline. |
| FR-010 | Release minify + shrinkResources. |
| FR-011 | Rejected-suggestion persistence skipped on suppressed/password/incognito fields. |
| FR-012 | Theme cycle persists without losing nav chrome / racing DataStore sync. |

## Out of scope

- EncryptedDataStore / Keystore wrapping of clipboard blobs (future).
- Full TalkBack parity for every long-press variant picker path.
- Glide typing, voice, translate, floating keyboard.

## Success Criteria

- Security review High/Medium/Low items closed in code.
- Bugbot High/Medium/Low items closed in code.
- `:app:testDebugUnitTest` and `:app:assembleRelease` (R8) green.
