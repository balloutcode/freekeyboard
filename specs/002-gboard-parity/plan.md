# Implementation Plan: 002 Gboard parity

**Branch**: `002-gboard-parity` | **Date**: 2026-11-07 | **As-built**: 2026-11-08

## Technical Context

- Stack: Kotlin, Compose IME, DataStore, minSdk 31 (unchanged from 001).
- Extends 001 IME core; no new network permissions.
- Follow-on: [003 inline autofill](../003-inline-autofill/), [004 privacy/hardening](../004-privacy-hardening/).

## Architecture

| Slice | Approach |
|-------|----------|
| Space cursor swipe | `SpaceCursorSwipe` + KeyGrid; `moveCursorBy` with DPAD on truncate/password |
| RU variants / dict | Expand `RuJcuiken` accents; expand/rank `SuggestionDictionary` |
| No-learn | `LearningPolicy` + `NoOpLearningStore` gate; password space mutation guard |
| Number pad | `NumberPad` + `KeyboardLayer.NUMBER` via `prefersNumberPad` |
| Password number row | Pref `number_row_for_passwords` → `effectiveNumberRow` |
| Toolbar / one-hand / clipboard | FeaturesMenu; one-hand rail; `ClipboardHistoryStore` (opt-in, 004 policy) |
| Suggestion reject | Long-press chip → session hide; persist via `RejectedSuggestionsStore` when safe |
| Incognito | Flag → strip icon; learning blocked |
| Prefs sync | `syncPreferencesFromStore` on show; theme `pendingTheme` (004) |

## Complexity Tracking

One-handed + clipboard are MVPs vs full Gboard; **keyboard resize deferred** (regression risk). Encrypted clipboard store deferred (004).
