# Agent 3 — In-scope matrix rescore (adversarial)

**Date**: 2026-11-07  
**Scope**: Every matrix row with `In-scope = yes` / `yes*` (128 rows). OOS `no` / `no*` excluded from coverage %.  
**Statuses**: Done | Partial | Missing | Incorrect | Regressed  
**Rule**: “Log says Done” without code/test/repro evidence → Partial or Missing.

**Emulator**: Yes (partial smoke) — `evidence/agent3-emulator-smoke.md`  
**Production code**: not modified.

---

## Method

1. Re-read Agent 1 artifacts + constitution + 001 contracts.  
2. Independently traced Agent 2 claim surfaces in `app/src/main/...` and unit tests.  
3. Smoke on emulator-5554 where feasible; otherwise static.  
4. Downgraded MVP / vacuous / chrome-only matches from Agent 2 “Done”.

---

## Coverage summary (Agent 3)

| Priority | In-scope | Done | Partial | Missing | Incorrect | Regressed | Done% | Done+Partial% |
|----------|---------:|-----:|--------:|--------:|----------:|----------:|------:|--------------:|
| **P0** | 75 | 68 | 5 | 0 | 2 | 0 | **90.7%** | 97.3% |
| **P1** | 41 | 28 | 9 | 4 | 0 | 0 | **68.3%** | 90.2% |
| **P2** | 12 | 3 | 4 | 5 | 0 | 0 | **25.0%** | 58.3% |
| **All** | 128 | 99 | 18 | 9 | 2 | 0 | **77.3%** | 91.4% |

Agent 2 claimed P0 5/5 closed this pass + zero remaining P0 Missing — **mostly hold**, but **A10-006 Incorrect** (strip chrome on suppress fields) and several Partials.

---

## Full In-scope rescore

Legend for Evidence: `C`=code path · `T`=unit test · `S`=emulator smoke · `001`=prior 001 baseline + contracts · `MVP`=behavior short of Gboard exact.

### Area 1 — Onboarding (P0/P1)

| ID | Pri | Agent2 | **Agent3** | Evidence |
|----|-----|--------|------------|----------|
| A01-001 | P0 | Done | **Done** | C: `AndroidManifest` + `method.xml`; package on emulator |
| A01-002 | P0 | Done | **Done** | C: `SettingsScreens` enable IME |
| A01-003 | P0 | Done | **Done** | C: select IME; S: default IME freekeyboard |
| A01-004 | P1 | Done | **Done** | C: `res/xml/method.xml` en-US/ru-RU |
| A01-005 | P1 | Done | **Done** | C: `settingsActivity` → `SettingsActivity` |

### Area 2 — Letters EN/RU

| ID | Pri | Agent2 | **Agent3** | Evidence |
|----|-----|--------|------------|----------|
| A02-001 | P0 | Done | **Done** | C: `EnQwerty.kt`; T: `EnQwertyTest`; S: QWERTY visible |
| A02-002 | P0 | Done | **Done** | C: `RuJcuiken.kt`; S: ЙЦУКЕН after lang switch |
| A02-003 | P0 | Done | **Done** | C: `KeyGrid` → `displayNameRu`; S: `Английский`/`Русский` |
| A02-004 | P0 | Done | **Partial** | C: bottom row exists; S: **emoji key not observed** on AVD (comma→globe). Pref may be off; chrome incomplete vs acceptance “all bottom controls” |
| A02-005 | P0 | Done | **Done** | C: `GboardMetrics`/`KeyGrid`; 001 QA |

### Area 3 — Symbols

| ID | Pri | Agent2 | **Agent3** | Evidence |
|----|-----|--------|------------|----------|
| A03-001 | P0 | Done | **Done** | C: `LAYER_SYMBOLS`; S: symbols page |
| A03-002 | P0 | Done | **Done** | C: `Symbols.kt`; S: 0–9 row |
| A03-003 | P0 | Done | **Done** | C: `symbolsAlt` + Shift on symbols |
| A03-004 | P0 | Done | **Done** | C: `onShiftLongPress` symbols branch |
| A03-005 | P0 | Done | **Done** | C: `LAYER_LETTERS`; S: ABC present |
| A03-006 | P0 | Done | **Done** | C: `Symbols.kt`; S: `@#$…` |
| A03-007 | P2 | Missing | **Missing** | No hold-?123-slide gesture |

### Area 4 — Number row

| ID | Pri | Agent2 | **Agent3** | Evidence |
|----|-----|--------|------------|----------|
| A04-001 | P0 | Done | **Done** | C: `LayoutOptions` number row; T: `LayoutOptionsTest`/`GboardMetricsTest` |
| A04-002 | P0 | Done | **Done** | C: prefs `number_row` |
| A04-003 | P0 | Done | **Done** | C: hints cleared when row on |
| A04-004 | P0 | Done | **Done** | C: digit hints + variants |
| A04-005 | P0 | Done | **Done** | C: `num_pad` `KeyAction.NOOP`; controller ignores NOOP |
| A04-006 | P1 | Done | **Done** | C: `numberRowForPasswords` + `effectiveNumberRow`; T: `PasswordNumberRowTest`; settings string |

### Area 5 — Shift / Caps

| ID | Pri | Agent2 | **Agent3** | Evidence |
|----|-----|--------|------------|----------|
| A05-001..005 | P0 | Done | **Done** | C: `shiftAfterTap` / `onShiftLongPress`; T: `ShiftStateTest`; S: Shift highlight |
| A05-006 | P0 | Done | **Done** | C: `AutoCapitalizer`; T: `AutoCapitalizerTest` |
| A05-007 | P1 | Done | **Done** | C: `auto_capitalization` pref |
| A05-008 | P0 | Done | **Done** | C: CAPS retained in `refreshAfterEdit` |
| A05-009 | P0 | Done | **Done** | C: `KeyAction.NOOP` → Unit (no consume) |

### Area 6 — Variants

| ID | Pri | Agent2 | **Agent3** | Evidence |
|----|-----|--------|------------|----------|
| A06-001 | P0 | Done | **Done** | C: `VariantPopup` + EN variants |
| A06-002 | P0 | Done | **Done** | C: Symbols `$` variants |
| A06-003 | P1 | Done | **Done** | C: digit fraction variants |
| A06-004 | P1 | Done | **Done** | C: `longPressDelayMs` slider |
| A06-005 | P1 | Done | **Done** | C: `LayoutOptions` clears variants when pref off |
| A06-006 | P0 | Done | **Done** | C: expanded `RuJcuiken` variants; T: `RuVariantsTest` (ё/ъ/і/ї/ґ/ў) |

### Area 7 — Space / globe

| ID | Pri | Agent2 | **Agent3** | Evidence |
|----|-----|--------|------------|----------|
| A07-001 | P0 | Done | **Done** | C: `onSpace` |
| A07-002 | P0 | Done | **Done** | S: labels |
| A07-003 | P0 | Done | **Done** | C: `requestLanguagePicker` + Space LP path in `KeyGrid` |
| A07-004 | P0 | Done | **Done** | C: `LANGUAGE` cycle; S: EN→RU |
| A07-005 | P1 | Done | **Done** | C: globe uses same LP path when wired |
| A07-006 | P0 | Done | **Done** | C: `showLanguageKey = enabledLanguages.size >= 2` |
| A07-007 | P0 | Done | **Partial** | C: `SpaceCursorSwipe` + `KeyGrid` gesture + `moveCursorBy`; T: `SpaceCursorSwipeTest`. **No live swipe smoke**; UTF-16 caret vs code-point edge cases unproven |

### Area 8 — Backspace

| ID | Pri | Agent2 | **Agent3** | Evidence |
|----|-----|--------|------------|----------|
| A08-001..005 | P0 | Done | **Done** | C: `InputConnectionFacade` / `WordDelete`; T: `WordDeleteTest` |

### Area 9 — Enter

| ID | Pri | Agent2 | **Agent3** | Evidence |
|----|-----|--------|------------|----------|
| A09-001..004 | P0 | Done | **Done** | C: `performEnter` / `EnterKeyStyle` / TYPE_NULL; T: interpreter tests |

### Area 10 — Suggestions

| ID | Pri | Agent2 | **Agent3** | Evidence |
|----|-----|--------|------------|----------|
| A10-001 | P0 | Done | **Done** | C: `SuggestionBar`/`SuggestionEngine`; S: Hello chip once |
| A10-002 | P0 | Done | **Done** | C: `acceptSuggestion` |
| A10-003 | P1 | Done | **Done** | C: `NextWordModel`; T: next-word cases |
| A10-004 | P1 | Done | **Done** | C: `ProfanityLexicon`; T: block tests |
| A10-005 | P1 | Done | **Done** | C: `show_suggestion_strip` |
| A10-006 | P0 | Done | **Incorrect** | C: `refreshAfterEdit` empties chips on suppress, but `KeyboardRoot` still mounts `SuggestionBar` when `showSuggestionStrip && LETTERS` — **strip chrome not hidden** for password/email/URI/NO_SUGGESTIONS (violates 001 FR-026 / acceptance “no strip”) |
| A10-007 | P1 | Done | **Partial** | C: LP reject → `RejectedSuggestionsStore` + engine filter. **No drag-to-trash UI** (Gboard exact). MVP OK, not Done vs matrix wording |
| A10-008 | P0 | Done | **Done** | C: strip gated to LETTERS layer |
| A10-010 | P0 | Done | **Done** | C: RU dict + ranking; T: `russianPrivPrefixSuggestsPrivet` |

### Area 11 — Toolbar / one-hand / resize

| ID | Pri | Agent2 | **Agent3** | Evidence |
|----|-----|--------|------------|----------|
| A11-001 | P1 | Done | **Partial** | C: `FeaturesMenu` + Apps affordance in `SuggestionBar`. S: menu **not opened** (coords). Offline-only OK; visual parity thin |
| A11-002 | P1 | Done | **Partial** | C: 44dp rail + `weight(1f)` body — **not true Gboard shrink/dock** (~full width minus rail) |
| A11-003 | P1 | Missing | **Missing** | No resize handles; `GboardMetrics` fixed |
| A11-004 | P1 | Done | **Done** | C: `cycleTheme` from FeaturesMenu |

### Area 12 — Emoji

| ID | Pri | Agent2 | **Agent3** | Evidence |
|----|-----|--------|------------|----------|
| A12-001 | P0 | Done | **Partial** | C: `EmojiPanel` + `KeyAction.EMOJI`. S: panel **not reached**; emoji key absent on AVD bottom row (pref?) |
| A12-002 | P1 | Done | **Done** | C: categories RU |
| A12-003 | P1 | Done | **Done** | C: `RecentEmojiStore`; T: store test |
| A12-004 | P0 | Done | **Done** | C: `letterLayerContentHeight` shared |
| A12-006 | P2 | Missing | **Missing** | No emoji search |
| A12-007 | P2 | Missing | **Missing** | No emoji size slider |
| A12-008 | P1 | Done | **Partial** | C: `showEmojiKey` filter. When off, FeaturesMenu has emoji entry — **unproven live**; risk of no entry if strip suppressed |

### Area 13 — Clipboard

| ID | Pri | Agent2 | **Agent3** | Evidence |
|----|-----|--------|------------|----------|
| A13-001 | P1 | Done | **Done** | C: `ClipboardHistoryStore` + `ClipboardPanel` + listener in IME service |
| A13-002 | P1 | Done | **Done** | C: pin toggle |
| A13-003 | P2 | Done | **Done** | C: `TTL_MS` prune; T: `ClipboardHistoryStoreTest` |

### Area 14 — Themes / feedback / height

| ID | Pri | Agent2 | **Agent3** | Evidence |
|----|-----|--------|------------|----------|
| A14-001..005 | P1 | Done | **Done** | C: themes + borders; S: dark charcoal |
| A14-006 | P1 | Partial | **Partial** | C: click sound only; no volume |
| A14-007 | P1 | Partial | **Partial** | C: fixed 18ms vibrate; no strength |
| A14-008 | P1 | Done | **Done** | C: `KeyPressOverlay` |
| A14-009 | P0 | Done | **Done** | C: `IME_NAV_STRIP_DP=54`; 001 QA |
| A14-010 | P1 | Missing | **Missing** | Same as A11-003 |

### Area 15 — Settings

| ID | Pri | Agent2 | **Agent3** | Evidence |
|----|-----|--------|------------|----------|
| A15-001 | P1 | Done | **Done** | C: RU `strings.xml` / SettingsScreens |
| A15-002 | P1 | Done | **Done** | C: `SettingsActivity` theme sync |
| A15-003 | P1 | Partial | **Partial** | New prefs added; volume/strength/resize/dict still missing; inventory stale |
| A15-004 | P1 | Done | **Done** | C: clipboard toggle in settings + IME panel |
| A15-005 | P2 | Missing | **Missing** | No personal dictionary screen |
| A15-008 | P0 | Done | **Done** | C: About; grep settings — no Gboard/Google brand |

### Area 16 — Editors

| ID | Pri | Agent2 | **Agent3** | Evidence |
|----|-----|--------|------------|----------|
| A16-001 | P0 | Done | **Incorrect** | Typing OK; **strip chrome still shown** when suppress (same root cause as A10-006) |
| A16-002 | P0 | Done | **Partial** | Suppress chips OK; strip chrome may remain |
| A16-003 | P0 | Done | **Partial** | Same |
| A16-004 | P0 | Done | **Done** | C: `RawKeyCodes`; T: `RawKeyCodesTest` |
| A16-005 | P1 | Partial | **Partial** | Cyrillic `commitText` fallback |
| A16-006 | P1 | Done | **Done** | C: `NumberPad` + `prefersNumberPad`; T: `NumberPadLayoutTest` |
| A16-007 | P0 | Done | **Done** | C: visible password → suppress |
| A16-008 | P0 | Done | **Done** | C: `NO_SUGGESTIONS` flag |

### Area 17 — Landscape

| ID | Pri | Agent2 | **Agent3** | Evidence |
|----|-----|--------|------------|----------|
| A17-001 | P0 | Done | **Done** | S portrait |
| A17-002 | P0 | Done | **Done** | C: `onEvaluateFullscreenMode=false`; S landscape |
| A17-003 | P0 | Done | **Done** | C: `onConfigurationChanged` |
| A17-004 | P1 | Done | **Done** | C: landscape metrics |

### Area 18 — Language persistence

| ID | Pri | Agent2 | **Agent3** | Evidence |
|----|-----|--------|------------|----------|
| A18-001..004 | P0 | Done | **Done** | C: DataStore last lang; `onStartInput` → LETTERS/NUMBER + Shift OFF |
| A18-005 | P1 | Done | **Done** | Product N/A; IME lang ≠ system |

### Area 19 — Privacy / learn

| ID | Pri | Agent2 | **Agent3** | Evidence |
|----|-----|--------|------------|----------|
| A19-001 | P1 | Done | **Done** | C: `isIncognito` + SuggestionBar icon; LearningPolicy flag |
| A19-002 | P0 | Done | **Partial** | T: `LearningPolicyTest` gates password. **No production call site** invokes `recordTypedToken` on commit — vacuous today; future learn path may bypass if not wired |
| A19-003 | P0 | Done | **Done** | Offline dict only; no network suggest |

### Area 20 — A11y

| ID | Pri | Agent2 | **Agent3** | Evidence |
|----|-----|--------|------------|----------|
| A20-001 | P2 | Partial | **Partial** | Some `contentDescription` |
| A20-002 | P2 | Partial | **Partial** | Chips clickable; TalkBack unproven |
| A20-003 | P2 | Missing | **Missing** | No font-scale adaptation |
| A20-004 | P2 | Missing | **Missing** | Switch Access unproven |

### Area 21 — Misc

| ID | Pri | Agent2 | **Agent3** | Evidence |
|----|-----|--------|------------|----------|
| A21-001..003 | P0/P1 | Done | **Done** | C: double-space helpers |
| A21-005 | P0 | Done | **Partial** | Duplicate of A07-007 — same Partial evidence |
| A21-006 | P0 | Done | **Done** | C: `onStartInput` Shift OFF |
| A21-007 | P2 | Done | **Done** | Stable coexistence assumed; no crash path found |
| A21-008 | P1 | Done | **Done** | C: NOOP skips feedback |
| A21-009 | P0 | Done | **Done** | C: lang toggle sets LETTERS |
| A21-010 | P0 | Done | **Done** | C: `onEmojiSelected` commitText |
| A21-011 | P2 | Missing | **Missing** | No composing span |
| A21-012 | P2 | Partial | **Partial** | Batch edit optional |

---

## 001 regression checklist (Agent 3)

| Check | Result | Evidence |
|-------|--------|----------|
| Caps double-tap / long-press | **Hold** | `SHIFT_DOUBLE_TAP_MS`, `onShiftLongPress` |
| Dark charcoal | **Hold** | `Color.kt` equal RGB; S |
| Settings theme follows KB | **Hold** | `SettingsActivity` |
| ~54dp insets | **Hold** | `IME_NAV_STRIP_DP=54` |
| TYPE_NULL RawKeyCodes | **Hold** | `RawKeyCodes` + tests |
| RU NOOP pad | **Hold** | `LayoutOptions` num_pad NOOP |
| Suggestion suppress | **Regressed chrome** | Chips empty, **bar still shown** |
| onStartInput → LETTERS | **Hold** | + NUMBER pad for number fields |
| Symbols Shift long-press | **Hold** | symbols branch |

## Hard OOS landed?

| Item | Present? |
|------|----------|
| Glide / swipe typing | **No** |
| Voice / translate / GIF / floating / cloud | **No** (FeaturesMenu comment + Models doc) |
| Google brand in settings | **No** |

---

## Status deltas vs Agent 2 (material)

| ID | Agent2 → Agent3 | Why |
|----|-----------------|-----|
| A10-006 | Done → **Incorrect** | Strip chrome not hidden on suppress fields |
| A16-001 | Done → **Incorrect** | Same |
| A16-002/003 | Done → **Partial** | Chrome leak |
| A07-007 / A21-005 | Done → **Partial** | Untested live; unit-only |
| A10-007 | Done → **Partial** | No trash chrome |
| A11-001/002 | Done → **Partial** | Thin MVP / no shrink |
| A12-001/008 | Done → **Partial** | Live emoji path weak |
| A02-004 | Done → **Partial** | Emoji key missing on AVD |
| A19-002 | Done → **Partial** | Gate unwired to typing path |
| A11-003 / A14-010 | Missing → **Missing** | Confirmed |
| A14-006/007 / A15-003 / A16-005 | Partial → **Partial** | Confirmed |
