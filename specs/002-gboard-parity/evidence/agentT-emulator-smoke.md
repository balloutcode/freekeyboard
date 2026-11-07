# Agent T — emulator smoke test (freekeyboard IME)

**Date**: 2026-11-07  
**Overall**: **FAIL** (11 PASS / 2 FAIL / 0 SKIP) — fixes landed after this run (space LP timeout + in-IME LanguagePicker; one-hand `fillMaxWidth(0.68f)` dock)  
**Device**: `emulator-5554` (`sdk_gphone64_x86_64`, 720×1280 @ 320dpi)  
**Package**: `com.freekeyboardballoutcode`  
**IME**: `com.freekeyboardballoutcode/.ime.FreekeyboardImeService` (enabled + default)  
**APK**: debug reinstalled via `adb install -r` (`app-debug.apk`, versionName `0.1.0`)  
**Hosts**: Messages “New chat” `ContactSearchField` (`To:`); Chrome HTML `agentT-field-test.html` (email/password/text); Chrome omnibox (URI)

## Scenario results

| # | Scenario | Result | Notes / evidence |
|---|----------|--------|------------------|
| 1 | IME enabled + default | **PASS** | `ime list -s` + `settings get secure default_input_method` → freekeyboard; `mCurMethodId` matches |
| 2 | Open EditText; keyboard shown | **PASS** | Messages `To:` focused; `mInputShown=true`. `agentT-01-keyboard-shown.png` |
| 3 | Type EN; switch RU; Cyrillic | **PASS** | Key-face `Hi` / `Hello`; globe → EN (`Английский`); RU ЙЦУКЕН; typed `Йцу`. `agentT-02`…`agentT-09` |
| 4 | Symbols (?123) + ABC return | **PASS** | Digits/`@#$…` then ABC → letters. `agentT-06-symbols.png`, `agentT-07-abc-return.png` |
| 5 | Space tap + **horizontal swipe moves caret** | **PASS** | Space inserts gap (`… В`). Swipe-left on space then marker → `YAsdf` (caret at start; no spaces inserted). `agentT-05-space-caret.png` |
| 6 | Long-press Space → language picker | **FAIL** | `adb` hold/`motionevent`/`swipe` same-point did not open `LanguagePicker` (Compose pointer path). Globe language toggle works. `agentT-14-lang-picker.png` (miss) |
| 7 | Emoji panel open/close | **PASS** | After enabling “Клавиша эмодзи” in settings: panel with “Поиск эмодзи”; ABC closes. `agentT-12-emoji.png`, `agentT-13-emoji-closed.png` |
| 8 | Suggestion strip on letters; **no strip on password/email** | **PASS** | Letters: strip + chips (`His`/`Him`, `Я`/`Не`/`Это`). Email/password HTML + URI omnibox: **no** freekeyboard strip chrome (Chrome autofill icons ≠ IME strip). `agentT-03`, `agentT-17*`, `agentT-19` |
| 9 | One-handed from features menu | **FAIL** | Features grid reachable (`Одной рукой` visible) `agentT-15-features.png`; tap did not produce visibly compressed one-hand dock. `agentT-16-one-handed.png` still full-width |
| 10 | **No** keyboard resize drag handle | **PASS** | No handle in all keyboard shots (01, 06, 11, 15, 21, 23) |
| 11 | **No** glide/gesture typing across letters | **PASS** | Top-row swipe did **not** commit a glide word (only incidental `2` from key contact). OOS feature correctly absent. `agentT-20-no-glide.png` |
| 12 | Dark theme charcoal (if switchable) | **PASS** | Settings “Тёмная” selectable; charcoal IME keys observed. `agentT-01`, `agentT-10`/`22`, `agentT-23-dark-keyboard.png` |
| 13 | Landscape briefly | **PASS** | `mCurrentOrientation=1`; docked soft KB, RU letters + strip. `agentT-21-landscape.png` |

**Counts**: PASS **11** · FAIL **2** · SKIP **0**

## Screenshot index

| File | Intent |
|------|--------|
| `agentT-01-keyboard-shown.png` | IME visible (charcoal), strip |
| `agentT-02-typing-en.png` | EN key typing (`Hi`) |
| `agentT-03-hello-suggestions.png` | Suggestions while typing |
| `agentT-04-space-swipe.png` / `agentT-05-space-caret.png` | Space / caret swipe |
| `agentT-06-symbols.png` | ?123 layer |
| `agentT-07-abc-return.png` | ABC → letters |
| `agentT-08-ru-layout.png` | RU layout capture |
| `agentT-09-cyrillic.png` | Cyrillic `Йцу` |
| `agentT-10-settings-dark.png` / `agentT-22-dark-settings.png` | Theme = dark |
| `agentT-11-kb-restored.png` | Emoji key + EN restored |
| `agentT-12-emoji.png` / `agentT-13-emoji-closed.png` | Emoji panel |
| `agentT-14-lang-picker.png` | Long-press attempt (miss) |
| `agentT-15-features.png` | Features menu |
| `agentT-16-one-handed.png` | One-handed attempt (no compress) |
| `agentT-17-password-field.png` / `agentT-17-uri-omnibox.png` / `agentT-18-email-field.png` / `agentT-19-text-with-strip.png` | Strip suppress |
| `agentT-20-no-glide.png` | No glide word |
| `agentT-21-landscape.png` | Landscape dock |
| `agentT-23-dark-keyboard.png` | Charcoal symbols layer |

## Blockers / gaps

1. **Space long-press → language picker** not exercised live: Compose long-press not reliably driven by `adb input` hold/swipe-in-place; needs instrumentation or real touch. Globe tap still switches language.
2. **One-handed mode** entry from Features did not yield a visible side-compressed keyboard in smoke taps (menu item present).
3. **Emoji key** defaults off until Settings “Клавиша эмодзи” is enabled (found during smoke).
4. Settings language toggles were intermittently flipped OFF during theme navigation; IME still typed with last language — leave EN+RU ON after sessions.
5. Coordinate taps on Compose IME are approximate; results are supporting smoke evidence, not full UI-test coverage.

## Method notes

- Reinstall: `adb install -r app\build\outputs\apk\debug\app-debug.apk`
- Key hits calibrated from `GboardMetrics` @ 320dpi (2px/dp); bottom row with emoji key weights.
- Space caret: type `asdf` → swipe space L→R/R→L → inject marker letter; `YAsdf` proved left swipe moved caret to start without inserting spaces.
- Password/email: local Node server + `adb reverse tcp:8765` → `agentT-field-test.html`.
