# Contract: IME ↔ Editor (InputConnection)

**As-built**: 2026-11-08

## Purpose

Defines observable obligations between freekeyboard IME and the focused editor via Android `InputConnection` / `EditorInfo`.

## Inputs (from editor / system)

- `EditorInfo.inputType`, `imeOptions`, `actionId`, `packageName`
- Selection and surrounding text when queried
- Configuration changes (orientation)

## Outputs (IME → editor)

| User action | IME obligation |
|-------------|----------------|
| Letter/symbol tap (normal field) | `commitText` (composing optional; 001 uses direct commit) |
| Backspace | Delete selection if any; else code-point-safe `deleteSurroundingTextInCodePoints` (fallback UTF-16 length via `utf16UnitsForCodePoints`) |
| Backspace hold (word) | Delete trailing whitespace run + preceding word token (`WordDelete`; read ≤1024 chars before caret) |
| Space | Commit space; if not suppressed/password: optional `. ` on double-space or autocorrect rewrite (002) |
| Enter | Multiline + NONE/UNSPECIFIED → `commitText("\n")`; else `performEditorAction`; TYPE_NULL → `KEYCODE_ENTER` |
| Emoji tap | `commitText` of emoji Unicode |
| Space caret swipe | Relative caret move; DPAD when password/suppress or text window truncated (≥512) |
| TYPE_NULL field | `RawKeyCodes` → `sendKeyEvent` down/up with meta; unmappable MAY `KeyEvent(ACTION_MULTIPLE)` / characters |

### TYPE_NULL key mapping (`RawKeyCodes`)

- `a–z` / `A–Z` → `KEYCODE_A…Z` (+ `META_SHIFT_ON` for uppercase)
- `0–9` → `KEYCODE_0…9`
- Space, Enter, Tab, and US punctuation (`. , / \ ; ' [ ] - = \`` and shifted `! @ # $ % ^ & * ( ) _ + { } | : " < > ? ~`)

## Suggestion visibility

**Word candidates** hidden when any of:

- User disabled `show_suggestion_strip`
- Layer is not LETTERS (symbols/number keep toolbar-only strip)
- `EditorInfoInterpreter.suppressSuggestions`: TYPE_NULL, password variations, non-`TYPE_CLASS_TEXT`, `TYPE_TEXT_FLAG_NO_SUGGESTIONS`, email / URI / filter / visible-password variations

**Features toolbar** remains available whenever the keyboard letter/symbols/number UI is shown (including suppressed fields). Inline autofill views may occupy the strip (003).

## Learning / mutation gates

- `LearningPolicy.mayLearnFromField` false for password / raw / suppressed / no-personalized-learning
- Autocorrect + double-space period skipped when `suppressSuggestions` or password
- Rejected-suggestion **persistence** skipped on suppressed/password/incognito (session UI may still hide a chip)

## Batching

Multi-step word delete uses `beginBatchEdit`/`endBatchEdit`. Nested calls MUST be balanced.

## Lifecycle

| Event | Obligation |
|-------|------------|
| `onStartInput` | Reset to LETTERS or NUMBER (if prefers number pad); Shift `OFF`; dismiss panels; clear inline autofill generation; refresh from surrounding text |
| `onStartInputView` | Nav chrome, prefs sync, clipboard capture install — **does not** re-run full `onStartInput` reset |
| `onFinishInputView` / `onWindowHidden` | Uninstall clipboard capture; tear down transient popups |
| Orientation change | Keep language; recompute layout metrics; fullscreen evaluation false |

## Non-goals

- Framework `onCreateCandidatesView` (candidates are Compose `SuggestionBar`)
- `commitContent` emoji MIME path (Unicode `commitText` is sufficient)
