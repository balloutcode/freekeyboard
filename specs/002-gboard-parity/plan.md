# Implementation Plan: 002 Gboard parity

**Branch**: `002-gboard-parity` | **Date**: 2026-11-07

## Technical Context

- Stack: Kotlin, Compose IME, DataStore, minSdk 31 (unchanged from 001).
- Extends 001 IME core; no new network permissions.

## Architecture

| Slice | Approach |
|-------|----------|
| Space cursor swipe | `SpaceCursorSwipe` + KeyGrid gesture; `InputConnectionFacade.moveCursorBy` |
| RU variants / dict | Expand `RuJcuiken` accents; expand/rank `SuggestionDictionary` |
| No-learn | `LearningPolicy` + `NoOpLearningStore` gate |
| Number pad | `NumberPad` layout + `KeyboardLayer.NUMBER` via `EditorInfoInterpreter.prefersNumberPad` |
| Password number row | Pref `number_row_for_passwords` → `effectiveNumberRow` |
| Toolbar / one-hand / clipboard | FeaturesMenu, one-hand rail, `ClipboardHistoryStore` |
| Suggestion reject | Long-press chip → `RejectedSuggestionsStore` |
| Incognito | Flag → strip icon; learning blocked |

## Complexity Tracking

One-handed + clipboard are MVPs vs full Gboard; resize deferred (regression risk / time).
