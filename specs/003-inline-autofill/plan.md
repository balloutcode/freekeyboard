# Implementation Plan: 003 Inline autofill

**Date**: 2026-11-07 | **As-built**: 2026-11-08

## Approach

| Piece | Detail |
|-------|--------|
| Manifest | `method.xml` `supportsInlineSuggestions=true` |
| Dependency | `androidx.autofill:autofill` (catalog) |
| Request | `InlineAutofillSupport.createInlineSuggestionsRequest` — themed specs, max 3 |
| Response | Inflate on main executor; generation counter drops stale callbacks |
| UI | `KeyboardUiState.inlineSuggestionViews` → `SuggestionBar` `AndroidView` |
| Prefer | Autofill chips over word chips even when `suppressSuggestions` |

## Constitution

FOSS AndroidX only; no credential logging; offline IME unchanged.
