# Agent 2b — freekeyboard atomic inventory

**Date**: 2026-11-07  
**Method**: CODE + unit tests + prior reports (`gboard-feature-matrix.md`, `agentS-screen-report.md`, `agentT-emulator-smoke.md`, `agent2-gap-backlog.md`, `out-of-scope.md`). Research only — no implementation.  
**Status legend**: **Done** = wired in production path · **Partial** = present but incomplete vs Gboard-like target · **Missing** = not present (in-scope gap) or intentionally absent (Hard OOS noted).

Must-verify items (1–6) re-checked against current sources below.

---

## Must-verify (CodePointer)

| # | Claim | Status | CodePointer |
|---|-------|--------|-------------|
| 1 | Space caret swipe L/R | **Done** | `SpaceCursorSwipe.kt`; `InputConnectionFacade.moveCursorBy`; `KeyGrid` space path uses `withTimeoutOrNull` LP deadline (stationary hold → picker; swipe → caret); `KeyboardController.onSpaceCursorSwipe`; `SpaceCursorSwipeTest` |
| 2 | Strip chrome on SYMBOLS | **Done** | `KeyboardRoot.kt` — when `!suppressSuggestions` and `layer == SYMBOLS` (also NUMBER / strip-off letters / incognito), mounts `SuggestionBar(suggestions = emptyList(), …)` (A10-008) |
| 3 | NumberPad variants + interpreter | **Done** | `NumberPad.numberLayout(allowDecimal/allowSigned/pinStyle)` + `phoneLayout`; `EditorInfoInterpreter.prefersNumberPad` / `isNumberPassword` / decimal+signed flags; `KeyboardController.activeLayout` NUMBER branch; `NumberPadLayoutTest`, `EditorInfoInterpreterTest` |
| 4 | One-hand ~68% dock + rail | **Done** | `KeyboardRoot.kt` — `fillMaxWidth(0.68f)` dock + `OneHandRail` expand/flip; prefs `oneHandedMode`/`oneHandedSide`; Agent T FAIL predates this dock width |
| 5 | LanguagePicker in-IME (not Popup) | **Done** | `LanguagePicker.kt` (explicit non-Popup sheet); `KeyboardRoot` `when (showLanguagePicker)` replaces key body; `requestLanguagePicker` from space LP |
| 6 | Suppress strip password/email/URI | **Done** | `EditorInfoInterpreter.suppressSuggestions` (password / visible password / email / URI / NO_SUGGESTIONS / non-text / TYPE_NULL); `KeyboardRoot` does **not** compose strip when `suppressSuggestions`; Agent T PASS + `EditorInfoInterpreterTest` |

---

## Inventory

| ID | Area | Feature | ExactBehavior | Interaction | Status | CodePointer | Notes |
|----|------|---------|---------------|-------------|--------|-------------|-------|
| A01-001 | 1 Onboarding | IME listed | Package appears as virtual keyboard under freekeyboard brand | Install APK → system Languages & input | Done | `AndroidManifest`, `res/xml/method.xml` | No Google name |
| A01-002 | 1 Onboarding | Enable IME | Opens system enable path | Settings → enable | Done | `SettingsScreens` enable | |
| A01-003 | 1 Onboarding | Select IME | Becomes default soft keyboard | System picker | Done | `SettingsScreens` select IME | |
| A01-004 | 1 Onboarding | Subtypes EN/RU | Declares en-US / ru-RU subtypes | System subtype UI | Done | `method.xml` | |
| A01-005 | 1 Onboarding | Settings deep link | Settings Activity from IME metadata | System keyboard settings | Done | `settingsActivity` / `SettingsActivity` | |
| A02-001 | 2 Letters | EN QWERTY | Latin QWERTY placement; tap commits | Tap letters | Done | `EnQwerty.kt`, `EnQwertyTest` | |
| A02-002 | 2 Letters | RU ЙЦУКЕН | Cyrillic JCUKEN; 11-key top row | Tap letters | Done | `RuJcuiken.kt` | |
| A02-003 | 2 Letters | Space language label | Space shows `Английский` / `Русский` | Visual | Done | `KeyGrid` + `displayNameRu` | |
| A02-004 | 2 Letters | Bottom row chrome | ?123, comma, emoji/globe, space, period, enter | Tap | Done | layouts + `LayoutOptions` | Emoji key pref-gated (Agent T: default off until enabled) |
| A02-005 | 2 Letters | Portrait hit targets | Full-width rows; Gboard-like metrics | Tap | Done | `GboardMetrics.kt`, `KeyGrid` | |
| A03-001 | 3 Symbols | Open ?123 | Switches to symbols layer | Tap ?123 | Done | `KeyAction.LAYER_SYMBOLS`, `KeyboardController` | |
| A03-002 | 3 Symbols | Digit commit | 0–9 from symbols top row | Tap | Done | `Symbols.kt` | |
| A03-003 | 3 Symbols | Alt symbols | Shift/`=\<` toggles alt page | Tap Shift on symbols | Done | `Symbols.altLayout`, `symbolsAlt` | |
| A03-004 | 3 Symbols | LP Shift = toggle alt | Long-press Shift on symbols toggles alt | Long-press | Done | `onShiftLongPress` | |
| A03-005 | 3 Symbols | ABC return | Returns to letter language | Tap ABC | Done | `LAYER_LETTERS` | Agent T PASS |
| A03-006 | 3 Symbols | Common punct | @ # $ % & - + ( ) / etc. | Tap | Done | `Symbols.kt` | |
| A03-007 | 3 Symbols | Hold ?123 slide | Hold-drag insert & return | Hold-drag | Missing | — | Optional / not required (`out-of-scope`, backlog) |
| A04-001 | 4 Number row | Toggle on | Dedicated 1–0 above letters | Pref | Done | `LayoutOptions` + `numberRow` | |
| A04-002 | 4 Number row | Toggle off | No dedicated row | Pref | Done | prefs | |
| A04-003 | 4 Number row | Clear digit hints when on | Letter faces lose corner digits | Auto with pref | Done | `LayoutOptions` | |
| A04-004 | 4 Number row | Digits via LP when off | Top-letter hints; LP inserts | Long-press | Done | variants/hints | |
| A04-005 | 4 Number row | RU 11th pad | Inert NOOP pad | Tap | Done | `num_pad` NOOP | |
| A04-006 | 4 Number row | Password-only number row | Pref forces row in passwords even if global off | Pref + password field | Done | `numberRowForPasswords`, `effectiveNumberRow` | |
| A05-001 | 5 Shift | One-shot capital | Shift → next letter upper → OFF | Tap Shift, letter | Done | `ShiftMode.ONCE` | |
| A05-002 | 5 Shift | Unlock ONCE | Second Shift cancels | Tap | Done | `shiftAfterTap` | |
| A05-003 | 5 Shift | Caps double-tap | Double-tap ~400ms → CAPS | Double-tap | Done | `SHIFT_DOUBLE_TAP_MS` | |
| A05-004 | 5 Shift | Caps long-press | Hold Shift → CAPS | Long-press | Done | `onShiftLongPress` | |
| A05-005 | 5 Shift | Unlock Caps | Tap Shift exits CAPS | Tap | Done | CAPS→OFF | |
| A05-006 | 5 Shift | Auto-capitalization | Sentence start asserts ONCE | Pref on | Done | `AutoCapitalizer` | |
| A05-007 | 5 Shift | Auto-cap off | Pref disables | Toggle | Done | `auto_capitalization` | |
| A05-008 | 5 Shift | CAPS persists | Stays until unlock | Type many | Done | `refreshAfterEdit` | |
| A05-009 | 5 Shift | NOOP ignores ONCE | RU pad does not consume ONCE | Tap NOOP | Done | `KeyAction.NOOP` | |
| A06-001 | 6 Variants | Accent popup | Hold vowel → accent row | LP + slide + release | Done | `VariantPopup`, EN variants | |
| A06-002 | 6 Variants | Currency currency | Hold `$` → € £ ¥… | Long-press | Done | Symbols variants | |
| A06-003 | 6 Variants | Digit fractions | Hold 1/2/3 → ½ ²… | Long-press | Done | Symbols | |
| A06-004 | 6 Variants | Delay preference | Touch & hold duration 100–700ms | Slider | Done | `longPressDelayMs` | |
| A06-005 | 6 Variants | Disable long-press | Pref off → no popup | Toggle | Done | `longPressForSymbols` | |
| A06-006 | 6 Variants | RU accents | Hold RU keys with variants | Long-press | Done | `RuJcuiken`, `RuVariantsTest` | |
| A07-001 | 7 Space | Commit space | Tap inserts U+0020 | Tap | Done | `SPACE` | Agent T PASS |
| A07-002 | 7 Space | Language label | Active language name on space | Visual | Done | `displayNameRu` | |
| A07-003 | 7 Space | Long-press language UI | Stationary hold opens in-IME picker | Long-press Space | Done | `KeyGrid` timeout LP → `requestLanguagePicker` → `LanguagePicker` | Agent T FAIL was adb/Compose; code path fixed with timeouts |
| A07-004 | 7 Globe | Tap toggles language | Cycles enabled langs | Tap globe | Done | `LANGUAGE` / `toggleOrPickLanguage` | Agent T PASS |
| A07-005 | 7 Globe | Long-press picker | Same picker path | LP globe | Done | space LP path | |
| A07-006 | 7 Globe | Hide if one language | Globe omitted when <2 enabled | Pref | Done | `showLanguageKey` | |
| A07-007 | 7 Space | Cursor swipe | Horizontal swipe moves caret; no layout change; tap still space | Swipe L/R on Space | Done | `SpaceCursorSwipe` + `moveCursorBy` + `KeyGrid` + `onSpaceCursorSwipe` | Agent T PASS; unit-tested; **must-verify #1** |
| A07-008 | 7 Keys | Emoji vs language XOR | Gboard mutual exclusion | Prefs | Missing | — | Product: fk allows both (`out-of-scope`); not a parity bug |
| A08-001 | 8 Backspace | Single delete | Deletes char/selection | Tap | Done | `deleteBackward` | |
| A08-002 | 8 Backspace | Repeat delete | Hold → rapid char delete | Hold <~900ms | Done | `KeyGrid` hold repeat | |
| A08-003 | 8 Backspace | Word delete | Extended hold deletes word+ws | Hold ≥~900ms | Done | `WordDelete.kt` | |
| A08-004 | 8 Backspace | Unicode-safe | Code-point deletes | Emoji/Cyrillic | Done | `deleteSurroundingTextInCodePoints` | |
| A08-005 | 8 Backspace | Selection delete | Clears selection first | Tap BS | Done | `InputConnectionFacade` | |
| A09-001 | 9 Enter | Newline | Multiline → newline | Tap Enter | Done | `performEnter` | |
| A09-002 | 9 Enter | IME_ACTION_SEND | performEditorAction SEND | Tap | Done | `EnterKeyStyle.SEND` | |
| A09-003 | 9 Enter | SEARCH/DONE/GO/NEXT | Matching action + chrome | Tap | Done | `enterKeyStyle` | |
| A09-004 | 9 Enter | TYPE_NULL Enter | KEYCODE_ENTER events | Tap | Done | `RawKeyCodes` / facade | |
| A10-001 | 10 Suggest | Show completions | ≤3 candidates above keys | Type prefix | Done | `SuggestionBar`, `SuggestionEngine` | Dict quality vs Gboard weaker (quality Partial historically; strip Done) |
| A10-002 | 10 Suggest | Accept suggestion | Tap replaces prefix + space | Tap chip | Done | `acceptSuggestion` | |
| A10-003 | 10 Suggest | Next-word | After word+space show next | Pref on | Done | `NextWordModel` | |
| A10-004 | 10 Suggest | Offensive filter | Block/deprioritize | Pref | Done | `ProfanityLexicon` | |
| A10-005 | 10 Suggest | Strip toggle | Pref hides strip | Toggle | Done | `show_suggestion_strip` | Empty chrome still possible via SYMBOLS path when strip pref off + letters? Letters+strip-off uses empty chrome branch |
| A10-006 | 10 Suggest | Suppress password etc. | No strip chrome for password/email/URI/NO_SUGGESTIONS/TYPE_NULL/non-text | Auto | Done | `suppressSuggestions` + `KeyboardRoot` gate | **must-verify #6**; Agent T PASS; Agent 3 Incorrect overturned by Agent S |
| A10-007 | 10 Suggest | Reject to trash | LP suggestion rejects | Long-press chip | Partial | `SuggestionBar` LP reject | LP only; no drag-to-trash chrome (Agent 3 note) |
| A10-008 | 10 Suggest | Strip on symbols chrome | Empty SuggestionBar chrome stays on ?123 | Layer change | Done | `KeyboardRoot` SYMBOLS branch | **must-verify #2** |
| A10-009 | 10 Suggest | Autocorrect underline | Auto-replace misspellings | Type | Partial | `autocorrectEnabled` + `SuggestionEngine.autocorrectCandidate` + BS undo | Pref exists; offline MVP; product-optional cloud Fix-it OOS |
| A10-010 | 10 Suggest | RU completions | Offline RU candidates | Type RU prefix | Done | `SuggestionDictionary` + frequency | Unit-tested `привет` |
| A11-001 | 11 Toolbar | Features grid | Offline shortcuts (theme/clipboard/one-hand/emoji/settings) | Tap apps on strip | Done | `FeaturesMenu` + `SuggestionBar` | No mic/search; Agent T reached menu |
| A11-002 | 11 Toolbar | One-handed mode | ~68% width dock L/R + rail expand/flip | Menu or prefs | Done | `KeyboardRoot` `fillMaxWidth(0.68f)` + `OneHandRail` | **must-verify #4**; Agent T FAIL pre-dock-width fix |
| A11-003 | 11 Toolbar | Resize | Drag height/width | — | Missing | removed | **Hard OOS** — correct absence |
| A11-004 | 11 Toolbar | Theme from toolbar | Cycle Light/Dark/OLED | Tap Theme | Done | `FeaturesMenu` `cycleTheme` | |
| A11-005 | 11 Toolbar | No Google Search | Must remain absent | — | Done | — | Hard OOS absent |
| A11-006 | 11 Toolbar | No mic | Must remain absent | — | Done | — | Hard OOS absent |
| A12-001 | 12 Emoji | Open panel | Emoji key / menu opens panel | Tap | Done | `EmojiPanel` | Agent T PASS after enabling key |
| A12-002 | 12 Emoji | Categories RU | Category tabs RU titles | Tap tabs | Done | `EmojiPanel` categories | |
| A12-003 | 12 Emoji | Recents | Недавние lists used | After insert | Done | `RecentEmojiStore` | |
| A12-004 | 12 Emoji | ABC return height | Same height ±1px | Tap ABC | Done | `letterLayerContentHeight` | |
| A12-005 | 12 Emoji | No GIF/sticker | Must remain absent | — | Done | — | Hard OOS absent |
| A12-006 | 12 Emoji | Search | Offline EN+RU aliases | Search field | Done | `EmojiSearch` + panel | |
| A12-007 | 12 Emoji | Size slider | Pref scales emoji faces | Settings | Done | `emojiFontScale` | |
| A12-008 | 12 Emoji | Hide emoji key | Pref removes key; menu still opens | Toggle | Done | `showEmojiKey` + `openEmojiFromMenu` | Default often off |
| A13-001 | 13 Clipboard | History panel | Local copied text list | Features → clipboard | Done | `ClipboardHistoryStore` + `ClipboardPanel` | Local-only MVP |
| A13-002 | 13 Clipboard | Pin | Pin persists | Pin toggle | Done | `ClipboardPanel` | |
| A13-003 | 13 Clipboard | Auto expiry | Unpinned prune ~1h | Time | Done | `TTL_MS` prune | |
| A13-004 | 13 Clipboard | Screenshots | Optional save shots | Pref | Missing | — | Privacy / default OOS |
| A14-001 | 14 Theme | Light | Light bed + keys | Select | Done | `ThemePreference.LIGHT` | |
| A14-002 | 14 Theme | Dark | Neutral charcoal greys | Select | Done | `DARK` | Agent T PASS |
| A14-003 | 14 Theme | OLED | Pure black | Select | Done | `OLED_DARK` | |
| A14-004 | 14 Theme | Persist | Sticky across sessions | Relaunch | Done | DataStore | |
| A14-005 | 14 Theme | Key borders | Optional 1dp outlines | Toggle | Done | `keyBorders` | |
| A14-006 | 14 Feedback | Sound | Click + volume | Pref/slider | Done | `KeyFeedback` + `soundVolume` | |
| A14-007 | 14 Feedback | Haptic | Vibration + strength | Pref/slider | Done | `KeyFeedback` + `hapticStrength` | |
| A14-008 | 14 Feedback | Popup | Circular preview above key | Pref | Done | `KeyPressOverlay` | |
| A14-009 | 14 Height | Bottom inset ~54dp | Gap screen bottom→spacebar | Visual | Done | `GboardMetrics` / insets | |
| A14-010 | 14 Height | User resize | Drag resize | — | Missing | removed | **Hard OOS** with A11-003 |
| A15-001 | 15 Settings | Russian UI | Settings strings RU | Open settings | Done | `strings.xml` / `SettingsScreens` | |
| A15-002 | 15 Settings | Theme follows KB | Settings themed | Open | Done | `SettingsActivity` | |
| A15-003 | 15 Settings | Preferences subset | Number row, feedback, LP, strip, etc. | Toggles/sliders | Done | `SettingsScreens`, `UserPreferencesRepository` | Flat list vs Gboard tree |
| A15-004 | 15 Settings | Clipboard screen | Toggle + IME panel | Settings / Features | Done | prefs + panel | |
| A15-005 | 15 Settings | Personal dictionary | Device-local list | Settings | Done | personalDictionary prefs + suggest | |
| A15-006 | 15 Settings | Privacy screen | Delete learned / personalize | — | Missing | — | Personalize OOS; delete when learn matures |
| A15-007 | 15 Settings | No Glide/Voice/Translate | Must not add | — | Done | — | Hard OOS absent |
| A15-008 | 15 Settings | About without Google | No Gboard trademarks | About | Done | about body | |
| A16-001 | 16 Editors | Password typing | Type chars; no strip | Focus password | Done | `isPassword` + suppress gate | |
| A16-002 | 16 Editors | Email field | No strip; `@` via symbols | Focus email | Done | suppress email | Agent T PASS |
| A16-003 | 16 Editors | URI field | No strip | Focus URI | Done | suppress URI | Agent T PASS omnibox |
| A16-004 | 16 Editors | TYPE_NULL raw keys | Latin/punct via KeyEvents | Type | Done | `RawKeyCodes` | |
| A16-005 | 16 Editors | TYPE_NULL Cyrillic | Fallback `commitText` when no keycode | Type RU | Partial | `InputConnectionFacade.sendKeyChar` | Stable; not Gboard-exact raw events — top Agent3 gap |
| A16-006 | 16 Editors | Number/phone pad | Digit pads: PIN / decimal / signed / phone | Focus NUMBER/PHONE | Done | `NumberPad` + `prefersNumberPad` / `isNumberPassword` | **must-verify #3** |
| A16-007 | 16 Editors | Visible password | Treat sensitive; suppress strip | Focus | Done | variations in suppress | |
| A16-008 | 16 Editors | NO_SUGGESTIONS flag | Hide strip | Flag | Done | `suppressSuggestions` | |
| A17-001 | 17 Orient | Portrait dock | Bottom IME | Portrait | Done | IME service | |
| A17-002 | 17 Orient | Landscape dock | No extract fullscreen | Rotate | Done | `onEvaluateFullscreenMode=false` | Agent T PASS |
| A17-003 | 17 Orient | Rotate mid-type | Keeps language | Rotate | Done | `onConfigurationChanged` | |
| A17-004 | 17 Orient | Landscape metrics | Tighter gaps | Auto | Done | `GboardMetrics` | |
| A17-005 | 17 Orient | Split keyboard | Tablet split | — | Missing | — | Tablets OOS |
| A18-001 | 18 Lang | Remember last | Reopen restores language | Hide/show | Done | `lastActiveLanguage` | |
| A18-002 | 18 Lang | Enabled set | Only enabled langs cycle | Prefs | Done | `enabledLanguages` | |
| A18-003 | 18 Lang | Cannot disable last | ≥1 language remains | Toggle | Done | `withLanguageEnabled` | |
| A18-004 | 18 Lang | New field resets layer | Letters; keeps language | onStartInput | Done | `onStartInput` | Number pad when `prefersNumberPad` |
| A18-005 | 18 Lang | System locale unchanged | IME lang ≠ system UI | Switch | Done | N/A product | |
| A19-001 | 19 Privacy | Incognito indicator | Flag shows icon; learning blocked | Editor flag | Done | `isIncognito` + SuggestionBar + `LearningPolicy` | |
| A19-002 | 19 Privacy | No learn in password | Passwords not learned | Auto | Done | `LearningPolicy.recordTypedToken` on space path | |
| A19-003 | 19 Privacy | Offline-only suggestions | No cloud ranking | Always | Done | suggest offline | |
| A19-004 | 19 Privacy | No federated learning UI | Must remain absent | — | Done | — | Hard OOS absent |
| A20-001 | 20 A11y | Key contentDescription | TalkBack names keys | Explore | Partial | `KeyGrid` `contentDescription = label` | Present; polish incomplete |
| A20-002 | 20 A11y | Suggestion announcements | Chips focusable/announce | TalkBack | Partial | `SuggestionBar` | Clickable; announcements incomplete |
| A20-003 | 20 A11y | Font scale | Keys coerce ~1–1.35 | System font | Done | `KeyGrid` fontScale | |
| A20-004 | 20 A11y | Switch Access | Specialized switch nav | — | Missing | — | P2; not verified |
| A21-001 | 21 Misc | Double-space period | `Hi`+space+space → `Hi. ` | Double Space | Done | `shouldInsertPeriodOnDoubleSpace` | |
| A21-002 | 21 Misc | No period after digits | `42  ` stays spaces | Double space | Done | token letter check | |
| A21-003 | 21 Misc | Pref disable double-space | Off → two spaces | Toggle | Done | `doubleSpacePeriod` | |
| A21-004 | 21 Misc | Undo autocorrect via BS | BS restores last auto-replace | Tap BS | Done | `KeyboardController` lastAutocorrect | Needs autocorrect pref on |
| A21-005 | 21 Misc | Cursor swipe Space | Same as A07-007 | Swipe | Done | shared `SpaceCursorSwipe` | |
| A21-006 | 21 Misc | Start input Shift OFF | New field Shift off | Focus | Done | `onStartInput` | |
| A21-007 | 21 Misc | Hard keyboard coexistence | Soft IME may show; no crash | Attach HW KB | Done | stable | |
| A21-008 | 21 Misc | Modifier feedback | Haptic/sound on specials; NOOP silent | Tap | Done | NOOP skips feedback | |
| A21-009 | 21 Misc | Rapid language switch | No wrong-script commits | Spam globe | Done | controller | |
| A21-010 | 21 Misc | Emoji Unicode commit | commitText emoji | Tap | Done | `onEmojiSelected` | |
| A21-011 | 21 Misc | Composing span | `setComposingText` underline | Type | Missing | direct commit | Optional / not required |
| A21-012 | 21 Misc | Batch edit multi-delete | begin/endBatchEdit wrapper | Word delete | Partial | `deleteWordBackward` without batch | Behavior OK |
| FK-LP-01 | 7 Space | Space LP timeout path | Stationary finger wakes via timeout (not `awaitPointerEvent` alone) | Hold still on Space | Done | `KeyGrid` `withTimeoutOrNull(remaining)` | Required for picker in IME; comment in code |
| FK-LANG-01 | 7 Picker | In-IME LanguagePicker | Column sheet inside IME; **not** PopupWindow | Space LP / globe LP | Done | `LanguagePicker.kt` | **must-verify #5** |
| FK-GLIDE | OOS | Glide typing | Gesture path across letter keys | Swipe letters | Missing | — | **Hard OOS** — correctly absent (Agent T PASS) |
| FK-COMMA | Misc | Long-press comma → Settings | Opens settings | LP comma / « | Done | `KeyGrid` / `onVariant` settings path + `KeyboardRoot` | Present; no dedicated matrix row historically |
| FK-NUM-PIN | 16 NumberPad | PIN pad | Compact digits; no ABC/Space; id `number_pad_pin` | TYPE_NUMBER_VARIATION_PASSWORD | Done | `NumberPad.numberLayout(pinStyle=true)` | |
| FK-NUM-DEC | 16 NumberPad | Decimal pad | `.` on 0-row | TYPE_NUMBER_FLAG_DECIMAL | Done | `allowDecimal` | |
| FK-NUM-SIGN | 16 NumberPad | Signed pad | `−` key; with decimal both shown | TYPE_NUMBER_FLAG_SIGNED | Done | `allowSigned` | |
| FK-NUM-PHONE | 16 NumberPad | Phone dial pad | * # + letter hints | TYPE_CLASS_PHONE | Done | `NumberPad.phoneLayout` | |


## Counts

| Status | Count |
|--------|------:|
| **Done** | **131** |
| **Partial** | **6** |
| **Missing** | **10** |
| **Total rows** | **147** |

- **Partial**: `A10-007`, `A10-009`, `A16-005`, `A20-001`, `A20-002`, `A21-012`
- **Missing**: `A03-007`, `A07-008`, `A11-003`, `A13-004`, `A14-010`, `A15-006`, `A17-005`, `A20-004`, `A21-011`, `FK-GLIDE`
  - Hard OOS / intentional absent: `A03-007`, `A07-008`, `A11-003`, `A14-010`, `FK-GLIDE`
  - Product-deferred: `A13-004`, `A15-006`, `A17-005`
  - In-scope P2: `A20-004`, `A21-011`

In-scope Done% (treating Hard-OOS Missing as non-gaps) aligns with Agent S **PASS_WITH_GAPS** (P0 100% in matrix).

---

## Suspected Partial / Missing for Agent 3

Highest-value suspects to adversarial-check (code may claim Done; smoke/product still weak):

| Priority | ID | Why suspect |
|----------|-----|-------------|
| **P1** | `A16-005` | Confirmed Partial — TYPE_NULL Cyrillic uses `commitText`, not raw KeyEvents |
| **P1** | `A07-003` / `FK-LP-01` | Timeout LP path in code; Agent T FAIL — needs live touch / instrumentation re-proof of LanguagePicker |
| **P1** | `A11-002` | `0.68f` dock + rail in code; Agent T FAIL pre-fix — needs emulator visual re-proof |
| **P1** | `A10-007` | LP reject only; no drag-to-trash chrome |
| **P1/P2** | `A10-009` | Autocorrect pref + candidate path exists; quality / underline UX may be MVP-thin |
| **P2** | `A20-001` / `A20-002` | `contentDescription` present; TalkBack polish incomplete |
| **P2** | `A20-004` | Switch Access Missing |
| **P2** | `A21-011` | No composing span (optional) |
| **P2** | `A21-012` | Word delete lacks `beginBatchEdit`/`endBatchEdit` |
| **P2** | `A15-006` | No dedicated privacy / delete-learned screen |
| **Smoke** | `A02-004` / `A12-008` | Emoji key defaults off — easy false “Missing emoji” in QA |
| **Not gaps** | Glide / resize / mic / Search / GIF / voice / translate | Hard OOS correctly absent |

**Must-verify for Agent 3**: all six CodePointer claims are **Done in source**; top residual risk is **live re-verification** of space LP → LanguagePicker and one-hand 68% dock (Agent T failures predating current code).
