# Contract: IME ↔ Editor (InputConnection)

**As-built**: 2026-11-07

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
| Backspace | Delete selection if any; else code-point-safe `deleteSurroundingTextInCodePoints` (fallback `deleteSurroundingText`) |
| Backspace hold (word) | Delete trailing whitespace run + preceding word token (`WordDelete.codePointsToDelete`) |
| Space | Commit space character (or `. ` on double-space when enabled and prior token is all letters) |
| Enter | `performEditorAction` when editor declares an action; else commit newline as appropriate; TYPE_NULL → `KEYCODE_ENTER` |
| Emoji tap | `commitText` of emoji Unicode |
| TYPE_NULL field | `RawKeyCodes.strokeFor(char)` → `sendKeyEvent` down/up with meta; unmappable (Cyrillic/emoji/…) MAY `commitText` |

### TYPE_NULL key mapping (`RawKeyCodes`)

- `a–z` / `A–Z` → `KEYCODE_A…Z` (+ `META_SHIFT_ON` for uppercase)
- `0–9` → `KEYCODE_0…9`
- Space, Enter, Tab, and US punctuation (`. , / \ ; ' [ ] - = \`` and shifted `! @ # $ % ^ & * ( ) _ + { } | : " < > ? ~`)

## Suggestion visibility

Strip is hidden when any of:

- User disabled `show_suggestion_strip`
- Layer is not LETTERS
- `EditorInfoInterpreter.suppressSuggestions`: TYPE_NULL, password variations, non-`TYPE_CLASS_TEXT`, `TYPE_TEXT_FLAG_NO_SUGGESTIONS`, email / URI / filter / visible-password variations

## Batching

Multi-step corrections (future) MUST use `beginBatchEdit`/`endBatchEdit`. For 001 single commits, batching is optional but nested calls MUST be balanced.

## Lifecycle

| Event | Obligation |
|-------|------------|
| `onStartInput` / `onStartInputView` | Reset to LETTERS; Shift `OFF`; `symbolsAlt=false`; dismiss language picker; refresh auto-cap / suggestions from surrounding text; language preference preserved |
| `onFinishInputView` | Tear down transient popups |
| Orientation change | Keep language; recompute layout metrics; keep fullscreen evaluation false; if input view restarts, start-input reset rules apply |

## Non-goals

- Autocorrect `commitCorrection` / personal learning dictionary
- Framework `onCreateCandidatesView` (candidates are Compose `SuggestionBar`, not the system candidates view)
- `commitContent` emoji MIME path (Unicode `commitText` is sufficient for 001)
