# Gboard feature matrix (Agent 2 TZ)

**Columns**: ID | Area | Feature | Gboard exact behavior | Interaction | Visual/chrome | Doc/source | Emulator evidence | fk status | Code pointers | In-scope | Priority | Acceptance test | Blockers/Notes

**Priority**: P0 daily EN+RU typing · P1 panels/settings/comfort · P2 rare/a11y  
**Status**: Missing / Partial / Done  
**Emulator**: emulator-5554 Gboard 18.5.6; many rows = docs+code (see evidence)

---

## Area 1 — Onboarding / enable IME

| ID | Area | Feature | Gboard exact behavior | Interaction details | Visual/chrome notes | Doc URL / source | Emulator evidence | freekeyboard status | Code pointers | In-scope | Priority | Acceptance test | Blockers/Notes |
|----|------|---------|----------------------|---------------------|---------------------|------------------|-------------------|---------------------|---------------|----------|----------|-----------------|----------------|
| A01-001 | 1 Onboarding | Appear in system IME list | After install, Gboard listed under virtual keyboards | System Settings → Languages & input | System UI; Gboard brand | support 6380730 | Package latin present | Done | AndroidManifest, method.xml | yes | P0 | Install APK; freekeyboard appears under freekeyboard brand (not Gboard) | No Google name |
| A01-002 | 1 Onboarding | Enable IME | User enables IME; system permission dialog | Tap enable in system settings | Security dialog | 6380730 | ime list includes fk | Done | SettingsScreens enable button | yes | P0 | From app settings, open system enable; toggle on | |
| A01-003 | 1 Onboarding | Select as current IME | Becomes default soft keyboard | System picker / Select IME | IME chooser | 6380730 | Was default before Gb switch | Done | SettingsScreens select IME | yes | P0 | Select freekeyboard; focus EditText → keyboard shows | |
| A01-004 | 1 Onboarding | Subtypes EN/RU | Declares language subtypes | System subtype UI | en-US, ru-RU tags | 001 contracts | method.xml | Done | res/xml/method.xml | yes | P1 | System shows EN and RU subtypes for freekeyboard | |
| A01-005 | 1 Onboarding | Settings deep link | Settings Activity from IME metadata | Open from system keyboard settings | Russian UI | 001 spec | — | Done | settingsActivity | yes | P1 | From system Gboard-like path, open freekeyboard settings | |

## Area 2 — Letter layouts EN/RU

| ID | Area | Feature | Gboard exact behavior | Interaction details | Visual/chrome notes | Doc URL / source | Emulator evidence | freekeyboard status | Code pointers | In-scope | Priority | Acceptance test | Blockers/Notes |
|----|------|---------|----------------------|---------------------|---------------------|------------------|-------------------|---------------------|---------------|----------|----------|-----------------|----------------|
| A02-001 | 2 Letters | EN QWERTY | Latin QWERTY letter placement | Tap letters commit | Gboard-like spacing | 2842292 / 001 | docs+code | Done | EnQwerty.kt | yes | P0 | Type `qwerty` in EN → correct string | |
| A02-002 | 2 Letters | RU ЙЦУКЕН | Cyrillic JCUKEN; 11-key top row | Tap letters commit | Top row 11 keys | 001 | docs+code | Done | RuJcuiken.kt | yes | P0 | Type `йцукен` in RU → correct | |
| A02-003 | 2 Letters | Spacebar language label | Space shows active language name | Visual only | EN/RU names | 001 / secondary | docs+code | Done | KeyGrid / language displayNameRu | yes | P0 | EN→`Английский`; RU→`Русский` on space | RU UI product choice |
| A02-004 | 2 Letters | Bottom row chrome | ?123, comma, emoji/globe, space, period, enter | Tap each | Modifier keys styled | surfaces-map | docs+code | Done | layouts | yes | P0 | All bottom controls present on letter layer | |
| A02-005 | 2 Letters | Key hit targets portrait | Full-width rows; keys tappable | Tap | ~40.5dp faces @320 | GboardMetrics / 001 QA | 001 QA | Done | GboardMetrics.kt, KeyGrid | yes | P0 | All letter keys respond in portrait 720×1280 | |

## Area 3 — Symbols / alt / digits

| ID | Area | Feature | Gboard exact behavior | Interaction details | Visual/chrome notes | Doc URL / source | Emulator evidence | freekeyboard status | Code pointers | In-scope | Priority | Acceptance test | Blockers/Notes |
|----|------|---------|----------------------|---------------------|---------------------|------------------|-------------------|---------------------|---------------|----------|----------|-----------------|----------------|
| A03-001 | 3 Symbols | Open ?123 | Switches to digits+punct | Tap ?123 | Layer swap | howtotypethis / 001 | docs+code | Done | LAYER_SYMBOLS | yes | P0 | From letters, ?123 shows digit row | |
| A03-002 | 3 Symbols | Digit commit | 0–9 insert | Tap | Top symbols row | 001 | docs+code | Done | Symbols.kt | yes | P0 | Type `1234567890` from symbols | |
| A03-003 | 3 Symbols | Alt symbols page | Shift/`=\<` toggles rarer symbols | Tap Shift on symbols | Label =\ or ?123 | 001 / secondary | docs+code | Done | symbolsAlt | yes | P0 | Shift shows alt; again returns primary | |
| A03-004 | 3 Symbols | Long-press Shift on symbols | Same as tap (toggle alt) | Long-press Shift | Must not no-op | 001 FR-008 | docs+code | Done | onShiftLongPress | yes | P0 | Long-press Shift on symbols toggles alt | |
| A03-005 | 3 Symbols | ABC return | Returns to letter language | Tap ABC | Bottom left | 001 | docs+code | Done | LAYER_LETTERS | yes | P0 | ABC restores EN or RU letters | |
| A03-006 | 3 Symbols | Common punct row | @ # $ % & - + ( ) / etc. | Tap | Mid rows | Symbols.kt | docs+code | Done | Symbols.kt | yes | P0 | Insert `$` and `@` from symbols | |
| A03-007 | 3 Symbols | Hold ?123 slide (optional Gb) | Hold ?123 slide to symbol inserts & returns | Hold-drag | Gesture | secondary | — | Missing | — | no | P2 | — | Optional; not required |

## Area 4 — Number row / hints

| ID | Area | Feature | Gboard exact behavior | Interaction details | Visual/chrome notes | Doc URL / source | Emulator evidence | freekeyboard status | Code pointers | In-scope | Priority | Acceptance test | Blockers/Notes |
|----|------|---------|----------------------|---------------------|---------------------|------------------|-------------------|---------------------|---------------|----------|----------|-----------------|----------------|
| A04-001 | 4 Number row | Toggle on | Dedicated 1–0 above letters | Prefs → Number row | Extra row height | androidauthority | docs+code | Done | numberRow LayoutOptions | yes | P0 | Enable; letter layer shows 1–0 | |
| A04-002 | 4 Number row | Toggle off | Digits via symbols / hints | Pref off | Shorter IME | same | docs+code | Done | prefs | yes | P0 | Disable; no dedicated row | |
| A04-003 | 4 Number row | Clear digit hints when on | Letter keys lose corner digit hints | Auto with pref | Clean letter faces | 001 FR-019 | docs+code | Done | LayoutOptions | yes | P0 | With row on, top letters show no digit hints | |
| A04-004 | 4 Number row | Digits via long-press when off | Top letters hint 1–0; LP inserts | Long-press | Hints visible | androidauthority | docs+code | Done | variants/hints | yes | P0 | Row off; LP `q`/`й` path inserts digit if mapped | |
| A04-005 | 4 Number row | RU 11th pad | Extra inert pad aligns columns | Tap pad | NOOP | 001 FR-025 | docs+code | Done | num_pad NOOP | yes | P0 | Tap pad: no text, no Shift consume | |
| A04-006 | 4 Number row | Password-only number row | Gb pref: number row in passwords | Pref | Password fields | howtogeek | not verified UI | Done | UserPreferences.numberRowForPasswords, KeyboardUiState.effectiveNumberRow | yes | P1 | With pref on: password field shows number row even if global off | |

## Area 5 — Shift / Caps / auto-cap

| ID | Area | Feature | Gboard exact behavior | Interaction details | Visual/chrome notes | Doc URL / source | Emulator evidence | freekeyboard status | Code pointers | In-scope | Priority | Acceptance test | Blockers/Notes |
|----|------|---------|----------------------|---------------------|---------------------|------------------|-------------------|---------------------|---------------|----------|----------|-----------------|----------------|
| A05-001 | 5 Shift | One-shot capital | Tap Shift → next letter upper → off | Tap Shift, tap letter | Shift highlight ONCE | 2842292 | docs+code | Done | ShiftMode.ONCE | yes | P0 | Shift+`a` → `A`; next `b` → `b` | |
| A05-002 | 5 Shift | Unlock ONCE | Tap Shift again cancels | Tap | Off | 001 | docs+code | Done | shiftAfterTap | yes | P0 | Shift twice slowly → no capital | |
| A05-003 | 5 Shift | Caps Lock double-tap | Double-tap Shift within ~400ms → CAPS | Double-tap | Locked indicator | 2842292 | docs+code | Done | SHIFT_DOUBLE_TAP_MS | yes | P0 | Double-tap; type `ABC`; stays upper | |
| A05-004 | 5 Shift | Caps via long-press | Hold Shift → CAPS | Long-press | Same end-state | 001 | docs+code | Done | onShiftLongPress | yes | P0 | Hold Shift; letters upper until unlock | |
| A05-005 | 5 Shift | Unlock Caps | Tap Shift once exits CAPS | Tap | Off | 2842292 | docs+code | Done | CAPS→OFF | yes | P0 | From CAPS, tap Shift → lower resumes | |
| A05-006 | 5 Shift | Auto-capitalization | Sentence start asserts ONCE | Pref on; after `. ` | Shift lit | 9058584 / 001 | docs+code | Done | AutoCapitalizer | yes | P0 | Empty field / after period → Shift ONCE | |
| A05-007 | 5 Shift | Auto-cap off | Pref disables | Toggle | | Settings | docs+code | Done | auto_capitalization | yes | P1 | Pref off; empty field Shift OFF | |
| A05-008 | 5 Shift | CAPS persists across commits | CAPS until dismiss | Type many letters | | 001 | docs+code | Done | refreshAfterEdit | yes | P0 | CAPS; type 5 letters all upper | |
| A05-009 | 5 Shift | NOOP does not consume ONCE | RU pad / empty | Tap NOOP | | 001 | docs+code | Done | KeyAction.NOOP | yes | P0 | ONCE + tap pad; next letter still upper | |

## Area 6 — Long-press variants

| ID | Area | Feature | Gboard exact behavior | Interaction details | Visual/chrome notes | Doc URL / source | Emulator evidence | freekeyboard status | Code pointers | In-scope | Priority | Acceptance test | Blockers/Notes |
|----|------|---------|----------------------|---------------------|---------------------|------------------|-------------------|---------------------|---------------|----------|----------|-----------------|----------------|
| A06-001 | 6 Variants | Accent popup | Hold vowel → accent row | Long-press, slide, release | Popup above key | 2842292 | docs+code | Done | VariantPopup / En variants | yes | P0 | EN hold `e` → pick `é` | |
| A06-002 | 6 Variants | Currency currency | Hold `$` → € £ ¥… | Long-press | | howtotypethis | docs+code | Done | Symbols variants | yes | P0 | Symbols hold `$` insert `€` | |
| A06-003 | 6 Variants | Digit fractions | Hold 1/2/3 → ½ ² etc. | Long-press | | howtotypethis | docs+code | Done | Symbols | yes | P1 | Hold `2` → `²` | |
| A06-004 | 6 Variants | Delay preference | Touch & hold duration | Slider 100–700ms | | engadget / prefs | docs+code | Done | longPressDelayMs | yes | P1 | Set 200ms; variants appear faster | |
| A06-005 | 6 Variants | Disable long-press | Pref off → no popup | Toggle | | prefs | docs+code | Done | longPressForSymbols | yes | P1 | Pref off; hold does not open popup | |
| A06-006 | 6 Variants | RU accent/extra letters | Hold RU keys with variants | Long-press | | 001 | docs+code | Done | RuJcuiken accents + RuVariantsTest | yes | P0 | Hold keys that declare variants; pick one | Expanded ё/ъ + acute/neighbors |

## Area 7 — Space / globe / picker / label

| ID | Area | Feature | Gboard exact behavior | Interaction details | Visual/chrome notes | Doc URL / source | Emulator evidence | freekeyboard status | Code pointers | In-scope | Priority | Acceptance test | Blockers/Notes |
|----|------|---------|----------------------|---------------------|---------------------|------------------|-------------------|---------------------|---------------|----------|----------|-----------------|----------------|
| A07-001 | 7 Space | Commit space | Tap Space inserts ` ` | Tap | | 2842292 | docs+code | Done | SPACE | yes | P0 | Tap space → U+0020 | |
| A07-002 | 7 Space | Language label | Shows active language | Visual | Russian names in fk | 001 | docs+code | Done | displayNameRu | yes | P0 | Label matches language | |
| A07-003 | 7 Space | Long-press language UI | Hold Space opens switch/picker | Long-press | Picker overlay | 2842292 | docs+code | Done | requestLanguagePicker | yes | P0 | Hold Space → pick RU/EN | Gb may auto-cycle; picker OK |
| A07-004 | 7 Globe | Tap toggles language | Globe cycles enabled langs | Tap | Bottom row | 2842292 prefs tip | docs+code | Done | LANGUAGE | yes | P0 | EN→globe→RU layout | |
| A07-005 | 7 Globe | Long-press picker | Same as Space LP | Long-press globe | | 001 | docs+code | Done | onSpaceLongPress path | yes | P1 | LP globe opens picker | |
| A07-006 | 7 Globe | Hide if one language | No globe when <2 enabled | Pref | Key omitted | 001 | docs+code | Done | showLanguageKey | yes | P0 | Disable RU; globe gone; still types EN | |
| A07-007 | 7 Space | Cursor swipe | Swipe L/R on Space moves cursor | Horizontal swipe | No layout change | 2842292 | docs+code | Done | SpaceCursorSwipe + moveCursorBy | yes | P0 | Place cursor mid-word; swipe space moves caret | Not glide typing |
| A07-008 | 7 Keys | Emoji vs language XOR | Gb: enabling one greys the other | Prefs | Mutual exclusion | 2842292 tip | not verified | Missing | — | no* | P2 | *Product decision: fk allows both; keep or match XOR | See out-of-scope |

## Area 8 — Backspace / word delete

| ID | Area | Feature | Gboard exact behavior | Interaction details | Visual/chrome notes | Doc URL / source | Emulator evidence | freekeyboard status | Code pointers | In-scope | Priority | Acceptance test | Blockers/Notes |
|----|------|---------|----------------------|---------------------|---------------------|------------------|-------------------|---------------------|---------------|----------|----------|-----------------|----------------|
| A08-001 | 8 Backspace | Single delete | Deletes char/selection | Tap | | platform IME | docs+code | Done | deleteBackward | yes | P0 | Tap BS deletes one code point | |
| A08-002 | 8 Backspace | Repeat delete | Hold → rapid char delete | Hold <~900ms | | 001 ui-keyboard | docs+code | Done | KeyGrid wordAfterMs | yes | P0 | Hold briefly deletes many chars | |
| A08-003 | 8 Backspace | Word delete | Extended hold deletes word+whitespace | Hold ≥~900ms | | 001 | docs+code | Done | WordDelete.kt | yes | P0 | `hello world ` hold BS → removes `world ` | |
| A08-004 | 8 Backspace | Unicode-safe | Deletes by code points | Emoji/Cyrillic | | 001 | docs+code | Done | deleteSurroundingTextInCodePoints | yes | P0 | Delete emoji / Cyrillic without orphan surrogates | |
| A08-005 | 8 Backspace | Selection delete | If selection, delete selection first | Tap BS | | InputConnection | docs+code | Done | InputConnectionFacade | yes | P0 | Select range; BS clears selection | |

## Area 9 — Enter / editor actions

| ID | Area | Feature | Gboard exact behavior | Interaction details | Visual/chrome notes | Doc URL / source | Emulator evidence | freekeyboard status | Code pointers | In-scope | Priority | Acceptance test | Blockers/Notes |
|----|------|---------|----------------------|---------------------|---------------------|------------------|-------------------|---------------------|---------------|----------|----------|-----------------|----------------|
| A09-001 | 9 Enter | Newline | Multiline field → newline | Tap Enter | Enter icon | EditorInfo | docs+code | Done | performEnter | yes | P0 | Multiline → newline | |
| A09-002 | 9 Enter | IME_ACTION_SEND | performEditorAction SEND | Tap | Send label/icon | EditorInfoInterpreter | docs+code | Done | EnterKeyStyle.SEND | yes | P0 | Chat-like field shows Send; fires action | |
| A09-003 | 9 Enter | SEARCH / DONE / GO / NEXT | Matching action | Tap | Styled key | same | docs+code | Done | enterKeyStyle | yes | P0 | Each imeOptions action updates key + fires | |
| A09-004 | 9 Enter | TYPE_NULL Enter | KEYCODE_ENTER events | Tap | | RawKeyCodes | docs+code | Done | InputConnectionFacade | yes | P0 | TYPE_NULL field receives Enter keyevents | |

## Area 10 — Suggestions strip

| ID | Area | Feature | Gboard exact behavior | Interaction details | Visual/chrome notes | Doc URL / source | Emulator evidence | freekeyboard status | Code pointers | In-scope | Priority | Acceptance test | Blockers/Notes |
|----|------|---------|----------------------|---------------------|---------------------|------------------|-------------------|---------------------|---------------|----------|----------|-----------------|----------------|
| A10-001 | 10 Suggest | Show completions | Up to 3 candidates above keys | Type prefix | Strip ~52dp | 7068415 / 001 | docs+code | Done | SuggestionBar | yes | P0 | Type `hel` → see Hello-like candidates | Dict quality Partial |
| A10-002 | 10 Suggest | Accept suggestion | Tap replaces prefix + space | Tap chip | | 7068415 | docs+code | Done | acceptSuggestion | yes | P0 | Tap candidate; word+space committed | |
| A10-003 | 10 Suggest | Next-word | After word+space show next | Pref on | | 7068415 | docs+code | Done | NextWordModel | yes | P1 | After `the ` see next-word chips | |
| A10-004 | 10 Suggest | Offensive filter | Block/deprioritize | Pref | | 001 | docs+code | Done | ProfanityLexicon | yes | P1 | With filter on, blocked terms absent | |
| A10-005 | 10 Suggest | Strip toggle | Pref hides strip | Toggle | | 9058584 | docs+code | Done | show_suggestion_strip | yes | P1 | Pref off → no strip | |
| A10-006 | 10 Suggest | Suppress password etc. | Hidden for password/email/URI/NO_SUGGESTIONS/TYPE_NULL | Auto | | 001 FR-026 | docs+code | Done | KeyboardRoot gates strip on suppressSuggestions | yes | P0 | Password field: no strip chrome | Strip not composed when suppress |
| A10-007 | 10 Suggest | Reject to trash | LP suggestion → drag Trash | Long-press | — | 7068415 | docs+code | Done | SuggestionBar LP reject | yes | P1 | LP unwanted chip rejects | LP only; no drag/swipe |
| A10-008 | 10 Suggest | Strip on symbols chrome | Strip chrome stays on ?123; chips empty | Layer change | | Gboard chrome | docs+code | Done | KeyboardRoot SYMBOLS SuggestionBar | yes | P0 | ABC→?123: top strip chrome remains | Emoji has own panel |
| A10-009 | 10 Suggest | Autocorrect underline | Auto-replace misspellings | Type | Underline | 7068415 | — | Missing | — | no* | P2 | *Needs product decision | out-of-scope |
| A10-010 | 10 Suggest | RU completions | Offline RU candidates | Type RU prefix | | 001 | docs+code | Done | SuggestionDictionary + frequency rank | yes | P0 | Type `прив` → useful RU candidates | Unit-tested привет |

## Area 11 — Toolbar / one-handed (offline-feasible)

| ID | Area | Feature | Gboard exact behavior | Interaction details | Visual/chrome notes | Doc URL / source | Emulator evidence | freekeyboard status | Code pointers | In-scope | Priority | Acceptance test | Blockers/Notes |
|----|------|---------|----------------------|---------------------|---------------------|------------------|-------------------|---------------------|---------------|----------|----------|-----------------|----------------|
| A11-001 | 11 Toolbar | Features grid entry | Grid opens shortcuts | Tap grid | Top-left | 6380730 | partial | Done | FeaturesMenu + SuggestionBar apps button | yes | P1 | Tap toolbar → menu without Google Search | Offline MVP; no mic/search |
| A11-002 | 11 Toolbar | One-handed mode | Shrink + dock L/R | Menu or prefs | Side rail | maketecheasier | — | Done | KeyboardRoot ~2/3 weight + rail | yes | P1 | Enable; keyboard docks; flip side; expand exits | ~2/3 dock |
| A11-003 | 11 Toolbar | Resize | Drag handles change height/gap | Resize tool | Handles | 9to5google | — | Missing | removed | no | P1 | — | Hard OOS: product removed resize |
| A11-004 | 11 Toolbar | Theme from toolbar | Open theme picker | Tap Theme | | 6102154 | — | Done | FeaturesMenu cycleTheme | yes | P1 | Change theme from keyboard chrome | Cycles Light/Dark/OLED |
| A11-005 | 11 Toolbar | No Google Search button | Gb Search chip | — | | 6380730 | — | Done (absent) | — | no | P0 | Must remain absent | OOS |
| A11-006 | 11 Toolbar | No mic | Voice key | — | | OOS | — | Done (absent) | — | no | P0 | Must remain absent | OOS |

## Area 12 — Emoji panel

| ID | Area | Feature | Gboard exact behavior | Interaction details | Visual/chrome notes | Doc URL / source | Emulator evidence | freekeyboard status | Code pointers | In-scope | Priority | Acceptance test | Blockers/Notes |
|----|------|---------|----------------------|---------------------|---------------------|------------------|-------------------|---------------------|---------------|----------|----------|-----------------|----------------|
| A12-001 | 12 Emoji | Open panel | Emoji key opens full panel | Tap emoji | Full height | 2842292 | docs+code | Done | EmojiPanel | yes | P0 | Open emoji; insert 😀 | |
| A12-002 | 12 Emoji | Categories RU | Category tabs | Tap tabs | Russian titles | 001 | docs+code | Done | categories | yes | P1 | Switch category; insert | |
| A12-003 | 12 Emoji | Recents | Недавние lists used | After insert reopen | | 001 | docs+code | Done | RecentEmojiStore | yes | P1 | Recent shows last used | |
| A12-004 | 12 Emoji | ABC return | Returns letters; no height jump | Tap ABC | Same height | 001 FR-027 | docs+code | Done | letterLayerContentHeight | yes | P0 | Height before/after equal (±1px) | |
| A12-005 | 12 Emoji | No GIF/sticker tabs | Gb has GIF | — | | OOS | — | Done (absent) | — | no | P0 | No GIF tab | OOS |
| A12-006 | 12 Emoji | Search | Search emoji | Tap search | | maketecheasier | docs+code | Done | EmojiSearch + EmojiPanel field | yes | P2 | Search `heart` finds ❤️ | Offline EN+RU aliases |
| A12-007 | 12 Emoji | Size slider | Pref emoji size | Settings | | 6102154 | docs+code | Done | emojiFontScale pref | yes | P2 | Slider changes emoji face size | |
| A12-008 | 12 Emoji | Hide emoji key | Pref | Toggle | | prefs | docs+code | Done | showEmojiKey | yes | P1 | Pref off; emoji key gone; still openable? (Gb via toolbar) | May need toolbar entry |

## Area 13 — Clipboard local

| ID | Area | Feature | Gboard exact behavior | Interaction details | Visual/chrome notes | Doc URL / source | Emulator evidence | freekeyboard status | Code pointers | In-scope | Priority | Acceptance test | Blockers/Notes |
|----|------|---------|----------------------|---------------------|---------------------|------------------|-------------------|---------------------|---------------|----------|----------|-----------------|----------------|
| A13-001 | 13 Clipboard | History panel | Copied text list | Open clipboard | Panel | maketecheasier | — | Done | ClipboardHistoryStore + ClipboardPanel | yes* | P1 | *Product decision; if yes: copy text → appears in panel → tap paste | Local-only MVP |
| A13-002 | 13 Clipboard | Pin | LP pin persists | Long-press Pin | Pinned section | howtogeek | — | Done | ClipboardPanel pin toggle | yes* | P1 | Pin survives clear window | |
| A13-003 | 13 Clipboard | Auto expiry | Unpinned clear ~1h | Time | | maketecheasier | — | Done | ClipboardHistoryStore.TTL_MS prune | yes* | P2 | Unpinned gone after TTL | |
| A13-004 | 13 Clipboard | Screenshots | Optional save shots | Pref | | howtogeek | — | Missing | — | no* | P2 | Privacy decision | Default off/OOS |

## Area 14 — Themes / feedback / height

| ID | Area | Feature | Gboard exact behavior | Interaction details | Visual/chrome notes | Doc URL / source | Emulator evidence | freekeyboard status | Code pointers | In-scope | Priority | Acceptance test | Blockers/Notes |
|----|------|---------|----------------------|---------------------|---------------------|------------------|-------------------|---------------------|---------------|----------|----------|-----------------|----------------|
| A14-001 | 14 Theme | Light | Light bed + keys | Select | Soft blue-grey fk | 6102154 / 001 | docs+code | Done | ThemePreference.LIGHT | yes | P1 | Light theme on KB + settings | |
| A14-002 | 14 Theme | Dark | Dark charcoal (fk: neutral greys) | Select | No blue slate | 001 | docs+code | Done | DARK | yes | P1 | Dark charcoal | |
| A14-003 | 14 Theme | OLED | Pure black | Select | #000 | 001 | docs+code | Done | OLED_DARK | yes | P1 | OLED pure black | |
| A14-004 | 14 Theme | Persist | Sticky across sessions | Relaunch | | 001 | docs+code | Done | DataStore | yes | P1 | Theme survives process death | |
| A14-005 | 14 Theme | Key borders | Optional outlines | Toggle | 1dp | 001 | docs+code | Done | keyBorders | yes | P1 | Borders on/off visible | |
| A14-006 | 14 Feedback | Sound | Click sound | Pref | | 6102154 | docs+code | Done | KeyFeedback + soundVolume | yes | P1 | Pref on → click sound | Volume slider |
| A14-007 | 14 Feedback | Haptic | Vibration on key | Pref | Device may override | 6102154 | docs+code | Done | KeyFeedback hapticStrength | yes | P1 | Pref on → haptic | Strength slider |
| A14-008 | 14 Feedback | Popup | Circular preview | Pref | Above key | 001 | docs+code | Done | KeyPressOverlay | yes | P1 | Pref on → popup on letter tap | |
| A14-009 | 14 Height | Bottom inset ~54dp | Gap screen bottom→spacebar | Visual | Nav strip reserve | 001 QA | 001 QA | Done | GboardMetrics / insets | yes | P0 | Measure ~54dp gap @320dpi | |
| A14-010 | 14 Height | User resize | Drag resize | Toolbar | | 9to5google | — | Missing | removed | no | P1 | — | Hard OOS with A11-003 |

## Area 15 — Full settings tree

| ID | Area | Feature | Gboard exact behavior | Interaction details | Visual/chrome notes | Doc URL / source | Emulator evidence | freekeyboard status | Code pointers | In-scope | Priority | Acceptance test | Blockers/Notes |
|----|------|---------|----------------------|---------------------|---------------------|------------------|-------------------|---------------------|---------------|----------|----------|-----------------|----------------|
| A15-001 | 15 Settings | Russian UI | All strings RU | Open settings | | 001 FR-018 | docs+code | Done | strings.xml | yes | P1 | No English chrome strings | |
| A15-002 | 15 Settings | Theme follows KB | Settings Activity themed | Open | | 001 | docs+code | Done | SettingsActivity | yes | P1 | OLED settings surfaces black | |
| A15-003 | 15 Settings | Preferences parity subset | Number row, feedback, LP… | Toggles | Flat list | inventory | docs+code | Done | SettingsScreens | yes | P1 | All Done prefs editable | Height/volume/haptic/emoji/autocorrect |
| A15-004 | 15 Settings | Clipboard screen | Dedicated nav | | | inventory | — | Done | Settings toggle + IME clipboard panel | yes* | P1 | Product decision | Toggle in settings; panel in IME |
| A15-005 | 15 Settings | Dictionary screen | Personal dict | | | 7068415 | docs+code | Done | personalDictionary prefs + suggest | yes* | P2 | Product decision | Device-local list |
| A15-006 | 15 Settings | Privacy screen | Delete learned / personalize | | | 12373137 | — | Missing | — | no | P2 | Personalize OOS; delete when learn exists | |
| A15-007 | 15 Settings | No Glide/Voice/Translate screens | Gb has them | | | OOS | — | Done (absent) | — | no | P0 | Must not add | OOS |
| A15-008 | 15 Settings | About without Google | No Gboard marks | | | FR-017 | docs+code | Done | about body | yes | P0 | About has no Google/Gboard trademark | |

## Area 16 — Password / email / URI / number / TYPE_NULL

| ID | Area | Feature | Gboard exact behavior | Interaction details | Visual/chrome notes | Doc URL / source | Emulator evidence | freekeyboard status | Code pointers | In-scope | Priority | Acceptance test | Blockers/Notes |
|----|------|---------|----------------------|---------------------|---------------------|------------------|-------------------|---------------------|---------------|----------|----------|-----------------|----------------|
| A16-001 | 16 Editors | Password typing | Can type; no suggestions | Focus password | Often no strip | platform | docs+code | Done | isPassword + suppress | yes | P0 | Password: type chars; no strip | |
| A16-002 | 16 Editors | Email field | No strip; @ reachable | Focus email | | FR-026 | docs+code | Done | suppress email | yes | P0 | Email: no strip; `@` via symbols | |
| A16-003 | 16 Editors | URI field | No strip | Focus URI | | FR-026 | docs+code | Done | suppress URI | yes | P0 | URI field no strip | |
| A16-004 | 16 Editors | TYPE_NULL raw keys | Latin/punct via KeyEvents | Type | | FR-024 | docs+code | Done | RawKeyCodes | yes | P0 | TYPE_NULL Space/`.`/`a` as keyevents | |
| A16-005 | 16 Editors | TYPE_NULL Cyrillic fallback | May commitText | Type RU | | 001 | docs+code | Partial | commitText fallback | yes | P1 | RU in TYPE_NULL does not crash; text appears | |
| A16-006 | 16 Editors | Number/phone pad | Digit-oriented layout | Focus number | Dial / PIN pad | platform IME + Gb | docs+code | Done | NumberPad variants + prefersNumberPad | yes | P1 | NUMBER/PIN/phone open digit pad; PIN has no ABC/Space | decimal/signed flags |
| A16-007 | 16 Editors | Visible password | Treat as sensitive | Focus | | EditorInfo | docs+code | Done | variations | yes | P0 | Visible password suppresses strip | |
| A16-008 | 16 Editors | NO_SUGGESTIONS flag | Hide strip | Flag | | FR-026 | docs+code | Done | suppressSuggestions | yes | P0 | Flag set → no strip | |

## Area 17 — Landscape / fullscreen

| ID | Area | Feature | Gboard exact behavior | Interaction details | Visual/chrome notes | Doc URL / source | Emulator evidence | freekeyboard status | Code pointers | In-scope | Priority | Acceptance test | Blockers/Notes |
|----|------|---------|----------------------|---------------------|---------------------|------------------|-------------------|---------------------|---------------|----------|----------|-----------------|----------------|
| A17-001 | 17 Orient | Portrait dock | Bottom IME | Portrait | | 001 | 001 QA | Done | IME service | yes | P0 | Portrait usable | |
| A17-002 | 17 Orient | Landscape dock | Bottom soft KB not extract | Rotate | Shorter keys | 001 FR-011 | docs+code | Done | onEvaluateFullscreenMode false | yes | P0 | Landscape: no fullscreen extract; keys tappable | |
| A17-003 | 17 Orient | Rotate mid-type | Keeps language; continues | Rotate | | 001 | docs+code | Done | onConfigurationChanged | yes | P0 | Type; rotate; continue same language | |
| A17-004 | 17 Orient | Landscape metrics | Tighter gaps | Auto | | GboardMetrics | docs+code | Done | GboardMetrics | yes | P1 | Landscape height uses landscape metrics | |
| A17-005 | 17 Orient | Split keyboard | Tablet split | | | howtogeek | — | Missing | — | no | P2 | Tablets OOS | |

## Area 18 — Language persistence

| ID | Area | Feature | Gboard exact behavior | Interaction details | Visual/chrome notes | Doc URL / source | Emulator evidence | freekeyboard status | Code pointers | In-scope | Priority | Acceptance test | Blockers/Notes |
|----|------|---------|----------------------|---------------------|---------------------|------------------|-------------------|---------------------|---------------|----------|----------|-----------------|----------------|
| A18-001 | 18 Lang | Remember last | Reopen restores language | Hide/show IME | | 2842292 / 001 | docs+code | Done | lastActiveLanguage | yes | P0 | Switch RU; hide; show → still RU | |
| A18-002 | 18 Lang | Enabled set | Only enabled langs cycle | Prefs | | 001 | docs+code | Done | enabledLanguages | yes | P0 | Disable EN; only RU available | |
| A18-003 | 18 Lang | Cannot disable last | At least one remains | Toggle | | 001 | docs+code | Done | withLanguageEnabled | yes | P0 | Cannot turn off last language | |
| A18-004 | 18 Lang | New field resets layer | Letters; keeps language | onStartInput | | FR-023 | docs+code | Done | onStartInput | yes | P0 | On symbols; focus new field → letters same lang | |
| A18-005 | 18 Lang | System locale unchanged | Switching IME lang ≠ system | Switch | | 2842292 | docs | Done (N/A) | — | yes | P1 | Switch RU; system UI language unchanged | |

## Area 19 — Incognito / no-learn

| ID | Area | Feature | Gboard exact behavior | Interaction details | Visual/chrome notes | Doc URL / source | Emulator evidence | freekeyboard status | Code pointers | In-scope | Priority | Acceptance test | Blockers/Notes |
|----|------|---------|----------------------|---------------------|---------------------|------------------|-------------------|---------------------|---------------|----------|----------|-----------------|----------------|
| A19-001 | 19 Privacy | Incognito indicator | Shows when no personalized learning | Editor flag / mode | Incognito icon | Android IME flag docs / Gb UI | — | Done | EditorInfoInterpreter.isIncognito + SuggestionBar icon | yes | P1 | When IME_FLAG_NO_PERSONALIZED_LEARNING set, show indicator & disable learning | LearningPolicy blocks |
| A19-002 | 19 Privacy | No learn in password | Passwords not learned | Auto | | 9058584 | docs+code | Done | LearningPolicy.recordTypedToken on space | yes | P0 | Password typing never writes learn DB | Wired on commit path |
| A19-003 | 19 Privacy | Offline-only suggestions | No cloud personalization | Always | | product | docs+code | Done | suggest offline | yes | P0 | Airplane mode; suggestions still work | |
| A19-004 | 19 Privacy | No federated learning UI | Gb Privacy toggles | — | | 12373137 | — | Done (absent) | — | no | P0 | Do not add Google personalize | OOS |

## Area 20 — Accessibility

| ID | Area | Feature | Gboard exact behavior | Interaction details | Visual/chrome notes | Doc URL / source | Emulator evidence | freekeyboard status | Code pointers | In-scope | Priority | Acceptance test | Blockers/Notes |
|----|------|---------|----------------------|---------------------|---------------------|------------------|-------------------|---------------------|---------------|----------|----------|-----------------|----------------|
| A20-001 | 20 A11y | Key contentDescription | TalkBack announces keys | Explore by touch | | Android a11y | docs+code | Partial | KeyGrid contentDescription | yes | P2 | TalkBack names letter keys | |
| A20-002 | 20 A11y | Suggestion announcements | Chips focusable | | | — | — | Partial | SuggestionBar | yes | P2 | TalkBack can focus/activate chips | |
| A20-003 | 20 A11y | Large display / font scale | Keys scale with system | Font size | | 6102154 emoji note | docs+code | Done | KeyGrid fontScale coerce 1–1.35 | yes | P2 | Font scale 1.3; keys still usable | |
| A20-004 | 20 A11y | Switch Access | Reachable controls | | | — | — | Missing | — | yes | P2 | Basic switch navigation works | |

## Area 21 — Misc (double-space, undo, cursor, …)

| ID | Area | Feature | Gboard exact behavior | Interaction details | Visual/chrome notes | Doc URL / source | Emulator evidence | freekeyboard status | Code pointers | In-scope | Priority | Acceptance test | Blockers/Notes |
|----|------|---------|----------------------|---------------------|---------------------|------------------|-------------------|---------------------|---------------|----------|----------|-----------------|----------------|
| A21-001 | 21 Misc | Double-space period | `  ` after letters → `. ` | Double-tap Space | Pref | 001 / prefs | docs+code | Done | shouldInsertPeriodOnDoubleSpace | yes | P0 | Type `Hi`+space+space → `Hi. ` | |
| A21-002 | 21 Misc | Double-space after digits | No period after digit tokens | Double space | | 001 | docs+code | Done | token.all isLetter | yes | P0 | `42  ` stays spaces | |
| A21-003 | 21 Misc | Pref disable double-space | Off | Toggle | | prefs | docs+code | Done | doubleSpacePeriod | yes | P1 | Pref off; two spaces remain | |
| A21-004 | 21 Misc | Undo autocorrect via BS | After auto-replace, BS undoes | Tap BS | | 7068415 | docs+code | Done | KeyboardController lastAutocorrect undo | yes | P2 | Pref on; typo+space; BS restores | Offline autocorrect |
| A21-005 | 21 Misc | Cursor swipe Space | See A07-007 | Swipe | | 2842292 | docs+code | Done | SpaceCursorSwipe (shared with A07-007) | yes | P0 | Same as A07-007 | |
| A21-006 | 21 Misc | Start input Shift OFF | New field Shift off | Focus | | FR-023 | docs+code | Done | onStartInput | yes | P0 | New field Shift OFF | |
| A21-007 | 21 Misc | Hard keyboard coexistence | Soft IME may show | Attach HW KB | | 001 assumptions | — | Done (stable) | show_ime_with_hard_keyboard | yes | P2 | No crash with HW keyboard | |
| A21-008 | 21 Misc | Press on modifiers feedback | Haptic/sound on specials | Tap | NOOP silent | 001 | docs+code | Done | NOOP skips feedback | yes | P1 | NOOP silent; Shift/BS feedback on | |
| A21-009 | 21 Misc | Rapid language switch stability | No wrong-script commits | Spam globe | | 001 edge | docs+code | Done | controller | yes | P0 | Rapid globe; typed chars match visible layout | |
| A21-010 | 21 Misc | Emoji Unicode commit | commitText emoji | Tap | | 001 | docs+code | Done | onEmojiSelected | yes | P0 | Emoji appears in editor | |
| A21-011 | 21 Misc | Composing span (optional) | Gb often uses composing | Type | Underline composing | Android IME | — | Missing | direct commit | yes | P2 | Optional; not required for P0 | |
| A21-012 | 21 Misc | Batch edit for multi-delete | begin/endBatchEdit | Word delete | | contract | docs+code | Partial | optional | yes | P2 | Word delete remains stable | |

---

## Tallies (Agent 1 → Agent 2 → **Agent S 2026-11-07**)

| Metric | Count |
|--------|------:|
| Matrix data rows (unique IDs) | **139** |
| Areas covered | **21 / 21** |
| In-scope (`yes`/`yes*`) | **126** (excludes Hard OOS `no`/`no*`, incl. resize) |
| P0 + In-scope + Missing | **0** |
| P0 + In-scope + Partial | **0** |
| P0 + In-scope Done% | **100%** (75/75) — Agent S |
| P1 + In-scope Done% | **97.4%** (38/39) — Agent S; only `A16-005` Partial |
| P1 resize `A11-003` / `A14-010` | **Hard OOS** (absent; not a parity gap) |
| Emulator used | **Yes** (partial; Agent 1); Agent S = code+tests |

### P0 Missing (actionable)

_None remaining._

### P0 Partial (actionable)

_None remaining._

### Remaining highest-value in-scope gaps (Agent S)

- `A16-005` TYPE_NULL Cyrillic `commitText` fallback (P1 Partial)
- P2 optional: TalkBack polish (`A20-001/002`), Switch Access (`A20-004`), composing span (`A21-011`), batch-edit (`A21-012`)

### Product / OOS reminders

- Space caret swipe `A07-007`/`A21-005` = **Done** (required).  
- Glide typing / resize / voice / translate / GIF / floating / cloud / Search / mic = **Hard OOS absent**.  
- See `agentS-screen-report.md` + `agentS-matrix-rescored.md`.
