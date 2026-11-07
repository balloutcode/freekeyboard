# freekeyboard baseline vs Gboard areas (001 as-built)

**Sources**: `specs/001-ime-core-en-ru/{spec,research,data-model,contracts}/*`, `app/src/main/java/com/freekeyboardballoutcode/**` (2026-11-07).  
**Verdict legend**: Done / Partial / Missing relative to **in-scope** Gboard behavioral parity.

## Area summary

| # | Area | Verdict | Notes |
|---|------|---------|-------|
| 1 | Onboarding / enable IME | **Done** | Settings buttons → system enable/select IME; subtypes en-US/ru-RU |
| 2 | Letter layouts EN/RU | **Done** | QWERTY + ЙЦУКЕН; RU 11-key top row |
| 3 | Symbols / alt / digits | **Done** | ?123 + Shift alt page; ABC return |
| 4 | Number row / hints | **Done** | Toggle; clears letter digit hints; RU NOOP pad |
| 5 | Shift / Caps / auto-cap | **Done** | Tap/double-tap(~400ms)/long-press Caps; auto-cap ONCE |
| 6 | Long-press variants | **Done** | Popup + delay slider; symbols currency/fractions |
| 7 | Space / globe / picker / label | **Partial** | Globe tap + Space LP picker + RU labels; **no** spacebar cursor swipe; globe LP = picker (Gboard often LP space to switch) |
| 8 | Backspace / word delete | **Done** | Repeat then word delete ~900ms; Unicode code points |
| 9 | Enter / editor actions | **Done** | SEND/SEARCH/DONE/GO/NEXT styles + performEditorAction |
| 10 | Suggestions strip | **Partial** | Offline ≤3; next-word; offensive filter; **no** reject-to-trash; weak dictionaries vs Gboard |
| 11 | Toolbar / one-handed | **Missing** | Suggestion bar only; no features grid / one-hand / resize / clipboard entry |
| 12 | Emoji panel | **Partial** | Offline categories + recents + ABC; no search/GIF/stickers (GIF OOS); no size slider |
| 13 | Clipboard local | **Missing** | No clipboard panel |
| 14 | Themes / feedback / height | **Partial** | Light/Dark/OLED + borders/sound/haptic/popup; fixed Gboard-like metrics; **no** resize/height UI |
| 15 | Full settings tree | **Partial** | Flat Russian settings covering prefs subset; missing Gboard branches (dict/privacy/clipboard/…) |
| 16 | Password / email / URI / number / TYPE_NULL | **Partial** | Suppress suggestions + TYPE_NULL raw keys; **no** dedicated number/phone layouts; password number-row-only pref Missing |
| 17 | Landscape / fullscreen | **Done** | `onEvaluateFullscreenMode=false`; metrics for landscape |
| 18 | Language persistence | **Done** | DataStore last language; enabled set; globe hide if <2 |
| 19 | Incognito / no-learn | **Missing** | No learning yet; does not read `IME_FLAG_NO_PERSONALIZED_LEARNING` / incognito UI |
| 20 | Accessibility | **Partial** | Some `contentDescription` on keys; no TalkBack polish / large-text mode |
| 21 | Misc (double-space, undo, cursor, …) | **Partial** | Double-space period Done; cursor swipe / undo Missing |

## Code map (primary pointers)

| Concern | Paths |
|---------|-------|
| IME service / fullscreen / insets | `ime/FreekeyboardImeService.kt` |
| Controller / Shift / suggestions / space | `ime/keyboard/KeyboardController.kt` |
| Layouts | `ime/keyboard/layouts/{EnQwerty,RuJcuiken,Symbols}.kt`, `LayoutOptions.kt` |
| UI root / layers | `ime/ui/KeyboardRoot.kt`, `KeyGrid.kt`, `EmojiPanel.kt`, `SuggestionBar.kt`, `LanguagePicker.kt` |
| Metrics | `ime/ui/GboardMetrics.kt` |
| InputConnection | `ime/input/{InputConnectionFacade,EditorInfoInterpreter,WordDelete,RawKeyCodes,AutoCapitalizer,KeyFeedback}.kt` |
| Suggest | `ime/suggest/*` |
| Prefs / settings | `data/{Models,UserPreferencesRepository}.kt`, `settings/ui/SettingsScreens.kt` |
| Theme | `ui/theme/*` |

## Prefs shipped (DataStore)

theme, key_borders, enabled_languages, last_language, recent_emoji, number_row, show_emoji_key, sound/haptic/popup, long_press_for_symbols + delay, show_suggestion_strip, next_word_suggestions, block_offensive_words, auto_capitalization, double_space_period.

## 001 QA already claiming Gboard-like (emulator 720×1280@320)

Hello + globe → да; Caps; symbols; space LP picker; double-space; suggestion accept; backspace hold; three themes — per `spec.md` Session 2026-11-06/07.
