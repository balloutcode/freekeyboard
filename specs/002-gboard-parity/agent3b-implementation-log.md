# Agent 3b — implementation log

**Date**: 2026-11-07
**Repo**: `c:\Users\alext\Documents\CursorRepos\freekeyboard\freekeyboard`
**Package/IME**: `com.freekeyboardballoutcode` / `.ime.FreekeyboardImeService`
**Scope**: close out Agent 2's remaining leftovers (`agent3b-parity-diff.md`); no git commit/push.

---

## Implemented

### 1. A16-005 — TYPE_NULL Cyrillic uses raw `KeyEvent`s, not `commitText` (P1)

- `ime/input/RawKeyCodes.kt`: added `sealed interface Action { Stroke(keyCode, metaState); Characters(text) }`
  and `actionFor(char)`, which returns `Stroke` when a US-QWERTY keycode exists, else `Characters`.
- `ime/input/InputConnectionFacade.kt`: `sendKeyChar` now dispatches on `RawKeyCodes.actionFor(char)`.
  The `Characters` branch (`sendCharacters`) sends a single
  `KeyEvent(now, text, KeyCharacterMap.VIRTUAL_KEYBOARD, 0)` — an `ACTION_MULTIPLE` /
  `KEYCODE_UNKNOWN` event carrying the literal Unicode text — instead of the previous
  `connectionProvider()?.commitText(...)` fallback. This is the same mechanism Android's own
  raw-key IME path uses for Unicode text with no keycode, and it's what `requiresRawKeys` editors
  (TYPE_NULL) are expected to honor even when they ignore `commitText`.
- No behavior change for ASCII letters/digits/punctuation (still per-key `sendDownUp`).

**Tests**: `RawKeyCodesTest` — 3 new cases (`cyrillic_resolvesToCharactersAction_notStroke`,
`emoji_resolvesToCharactersAction`, `asciiLetter_resolvesToStrokeAction`); existing 3 cases untouched.

### 2. A21-012 — Word-delete batch edit (P2, cheap)

- `ime/input/InputConnectionFacade.kt`: `deleteWordBackward()` body now wrapped in
  `ic.beginBatchEdit()` / `try { … } finally { ic.endBatchEdit() }`, so the (possibly multi-step)
  selection-clear + delete sequence reaches the host as one atomic batch instead of several
  intermediate `InputConnection` calls.
- Pure delete-boundary logic (`WordDelete.codePointsToDelete`) is unchanged.

**Tests**: no new test (would need an `InputConnection` test double not present in this project's
test harness — no Robolectric/Mockito dependency). Verified via existing `WordDeleteTest` (unchanged,
still green) plus emulator smoke (hold-backspace still deletes word + trailing whitespace).

### 3. A20-001 / A20-002 / A20-004 — Accessibility: TalkBack descriptions + Switch Access activation (P2, cheap)

- New file `ime/ui/KeyAccessibility.kt`: pure `describe(key, shift, enterStyle, label)` function
  mapping each `KeyAction` to a readable Russian description (Shift/Caps state-aware, Enter-style-aware
  "Отправить"/"Искать"/"Готово"/"Перейти"/"Далее"/"Новая строка", "Удалить", "Эмодзи", "Смена языка",
  space includes the active language name). Letter/digit/punctuation keys keep their visible glyph.
  Returns `null` for `KeyAction.NOOP` (inert RU number-row pad spacer).
- `ime/ui/KeyGrid.kt`:
  - Computes `accessibilityLabel` per key via `KeyAccessibility.describe(...)`.
  - `KeyButton` now takes `accessibilityLabel: String?` and attaches
    `Modifier.semantics(mergeDescendants = true) { contentDescription = …; onClick { onTap(); true } }`
    to the key's interactive `Box` when non-null, or `Modifier.clearAndSetSemantics { hideFromAccessibility() }`
    when null (NOOP). This runs **alongside**, not instead of, the existing `pointerInput` gesture —
    TalkBack/Switch Access dispatch an accessibility click action rather than a real touch event, so
    this adds support for those services without touching any touch/gesture code path.
  - The inner `Icon`'s own `contentDescription` is now `null` (decorative) since the parent `Box`
    fully describes the key — avoids duplicate/garbled TalkBack announcements.
- `ime/ui/SuggestionBar.kt`: each non-empty chip `Box` now also carries
  `Modifier.semantics { contentDescription = "Предложение: $word"; onClick("Выбрать"){ onSelect(word) }; customActions = [CustomAccessibilityAction("Отклонить"){ onReject(word) }] }`
  alongside the existing `pointerInput` tap/long-press. This gives TalkBack/Switch Access both
  "accept" (click action) and "reject" (custom action — same effect as the existing long-press)
  without any drag/swipe chrome (A10-007 drag-to-trash remains explicitly out of scope).

**Tests**: new `KeyAccessibilityTest` (6 cases: NOOP hidden, Shift modes, Backspace, Enter styles,
Space+language label, letter key keeps glyph). Semantics wiring itself is Compose UI and not covered
by a new instrumented test in this pass (no `androidTest` UI-semantics test added); regression-checked
via rebuild + reinstall + emulator screenshot (keyboard renders identically, no crash).

---

## Skip-OOS (explicit product decisions, no code change)

- **A10-007** drag-to-trash suggestion chrome — `out-of-scope.md`: "Optional; not required for parity."
  LP-reject (already Done) plus the new accessibility custom-action reject cover the functional need.
- **A03-007** hold-`?123`-slide insert-and-return — `out-of-scope.md`: "Optional; not required."

## Deferred (documented rationale in `agent3b-parity-diff.md`)

- **A10-009** autocorrect visual underline — functional autocorrect (replace-on-space + BS-undo) is
  already shipped; the underline affordance needs `setComposingText`, which is the same risky,
  cross-cutting change as the next item. Deferred together.
- **A21-011** composing-span (`setComposingText`) — explicitly marked optional/not-required in the
  matrix; switching the whole commit pipeline off direct `commitText` risks the TYPE_NULL raw-key
  path, autocorrect-undo, and suggestion-strip timing for a P2 feature. Deferred.
- **A15-006** privacy / delete-learned-data screen — freekeyboard has **no** personalization store
  (`LearningPolicy` + `NoOpLearningStore` are explicitly vacuous); a "delete learned data" screen
  would have nothing real to delete. Deferred until an actual on-device learning store exists.

---

## Must-verify regression check (no code touched; confirmed intact)

Space caret swipe, SYMBOLS strip chrome, NumberPad variants + `EditorInfoInterpreter`, one-hand
`0.68f` dock, in-IME `LanguagePicker`, strip-suppress on password/email/URI/NO_SUGGESTIONS — none
of the touched files (`InputConnectionFacade.kt`, `RawKeyCodes.kt`, `KeyGrid.kt`, `SuggestionBar.kt`,
new `KeyAccessibility.kt`) overlap these code paths' *logic*; `KeyGrid.kt`/`SuggestionBar.kt` changes
are additive semantics modifiers layered on top of the existing gesture code, not replacements.

---

## Build / test / install results

### `gradlew.bat :app:assembleDebug :app:testDebugUnitTest`

```
JAVA_HOME = C:\Program Files\Android\Android Studio\jbr
ANDROID_HOME = %LOCALAPPDATA%\Android\Sdk
BUILD SUCCESSFUL
```

- `assembleDebug`: **SUCCESS** (one pre-existing, unrelated deprecation warning for
  `Icons.Filled.ArrowForward`; the `invisibleToUser()` deprecation introduced mid-session was fixed
  by switching to `hideFromAccessibility()` before the final build).
- `testDebugUnitTest`: **SUCCESS** — all 24 test classes, **0 failures, 0 errors**, including:
  - `RawKeyCodesTest`: 6/6 (3 pre-existing + 3 new A16-005 cases)
  - `KeyAccessibilityTest`: 6/6 (new)
  - `WordDeleteTest`, `SpaceCursorSwipeTest`, `EditorInfoInterpreterTest`, `NumberPadLayoutTest`,
    `PasswordNumberRowTest`, `KeyboardControllerStateTest`, `LayoutOptionsTest`, `ShiftStateTest`,
    `EnQwertyTest`, `RuVariantsTest`, `SuggestionEngineTest`, `ProfanityPriorityTest`,
    `AutoCapitalizerTest`, `LearningPolicyTest`, `EmojiSearchTest`, `GboardMetricsTest`,
    `ClipboardHistoryStoreTest`, `RecentEmojiStoreTest`, `UserPreferencesTest`: all unchanged, all green.

### Install

```
adb -s emulator-5554 install -r app\build\outputs\apk\debug\app-debug.apk
Performing Streamed Install
Success
```

### Emulator smoke (regression only, not a full re-qual)

- Enabled + selected `com.freekeyboardballoutcode/.ime.FreekeyboardImeService` as default IME
  (was Gboard, left over from Agent 1b's live-exercise session).
- Opened Messages conversation text field → `mInputShown=true`, `mCurMethodId` = freekeyboard.
- Screenshot confirms: RU ЙЦУКЕН layout, suggestion strip with chips (Я / Не / Это), dark theme,
  bottom chrome (?123, comma, emoji, globe, space="Русский", period, Send) — all rendering correctly,
  no crash. `logcat` shows the app process started and `KeyGridKt.KeyGrid(...)` JIT-compiled
  (i.e. actually ran/recomposed) with no `FATAL`/`AndroidRuntime` entries for
  `com.freekeyboardballoutcode`.
- Evidence: `evidence/agent3b-04-keyboard.png` (RU layout + strip), `evidence/agent3b-06-final.png`
  (unrelated Messages-app "attachments not supported" dialog from a mistapped coordinate — app-level
  noise, not an IME issue; confirms app stayed alive and responsive throughout).

**Per coordinator instructions: IME was left selected as freekeyboard for this smoke test and was
NOT restored to Gboard — the coordinator handles IME restoration.**

---

## Return summary

- **Implement**: 4 (A16-005; A21-012; combined A20-001/002/004 a11y change-set)
- **Skip-OOS**: 2 (A10-007 drag chrome; A03-007 hold-?123-slide)
- **Defer**: 3 (A10-009 underline polish; A15-006 privacy screen; A21-011 composing span)
- **Verified, no regression**: 6 must-verify rows + all pre-existing unit tests
- **Tests**: 24 test classes, 0 failures/errors (9 new assertions added across `RawKeyCodesTest` + `KeyAccessibilityTest`)
- **Build**: `assembleDebug` + `testDebugUnitTest` → **SUCCESS**
- **Install**: `adb install -r` on `emulator-5554` → **Success**
- **Blockers**: none. No git commit/push performed (per instructions). IME left selected as
  freekeyboard on the emulator for the coordinator to restore.
