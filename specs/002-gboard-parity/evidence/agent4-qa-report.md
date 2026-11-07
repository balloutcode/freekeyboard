# Agent 4 — Full emulator QA, bug hunt, user simulation

**Date**: 2026-11-07
**Role**: Agent 4 in the freekeyboard Gboard-parity pipeline. Research + QA only — no git commit/push.
**Repo**: `c:\Users\alext\Documents\CursorRepos\freekeyboard\freekeyboard`
**Package/IME**: `com.freekeyboardballoutcode` / `.ime.FreekeyboardImeService`
**Device**: `emulator-5554` (NoJunkie `Medium_Phone`, `sdk_gphone64_x86_64`), 720×1280 @ 320dpi
**Inputs read**: `agent3b-parity-diff.md`, `agent3b-implementation-log.md`, `agent2b-freekeyboard-inventory.md`,
`out-of-scope.md`, prior `evidence/agentT-emulator-smoke.md`.

## Overall verdict: **PASS**

All 10 required scenarios + several bonus checks executed live on-device with screenshot evidence.
**0 Blocker, 0 High, 1 Medium, 1 Low** bugs filed (see `agent4-bugs.md`). No crashes, no ANRs,
no `FATAL EXCEPTION`/`AndroidRuntime` entries for `com.freekeyboardballoutcode` in `logcat` across the
entire session (~1.5h of live interaction). freekeyboard **remains the default IME** at hand-off.

## Environment note (not a freekeyboard bug)

The emulator process (`emulator.exe`) exited unexpectedly mid-session (scenario 7, after ~45 min of
uptime) — `adb devices` returned empty and no `emulator`/`qemu` process was found. This is emulator/
host infra flakiness, not reproducible via any freekeyboard action, and unrelated to the app under
test. Per the `emulator-nojunkie` skill, relaunched using the exact NoJunkie recipe
(`-gpu swiftshader_indirect -feature -Vulkan -feature -GLDirectMem -cores 3 -memory 3072`,
`QT_OPENGL=software`), waited for `sys.boot_completed=1`, re-enabled + re-selected freekeyboard as
IME, and resumed testing from scenario 7 onward with no data loss (APK and app data persisted on the
AVD disk). All scenarios below were completed either before or after this restart.

## Methodology

- `adb` + `screencap` + `uiautomator dump` for ground truth; Chrome + a tiny local Node static server
  (`agentT-server.js`, reused from Agent T) + `adb reverse tcp:8765` for email/password/text/number/
  tel/PIN field variants not present in any stock app. New test page `evidence/agent4-numpad-test.html`
  added for `type=number` / `type=tel` / `password+inputmode=numeric`.
- **Coordinate calibration caveat**: screenshots are read at a down-scaled preview; the actual PNG is
  720×1280 (confirmed via `System.Drawing.Image` size check). All device-pixel taps in this report use
  the `preview_coord × 1.25` scale factor. Early in the numpad scenario this scale was *not* applied
  consistently, which looked like a freekeyboard focus bug (number/tel fields "not focusing") — it
  was a test-script coordinate bug, not an app bug. Re-verified with corrected scaling: all three
  `NumberPad` variants work correctly (see Scenario 7). Flagged as a process learning, not filed as
  an app bug.
- Every scenario has at least one screenshot in `evidence/agent4-*.png`; UI XML dumps included where
  they added ground truth (focus state, window ownership for the LanguagePicker check).

## Scenario results (10 required + extras)

| # | Scenario | Result | Evidence |
|---|----------|--------|----------|
| 1 | IME default = freekeyboard | **PASS** | `settings get secure default_input_method` + `ime list -s` both show `com.freekeyboardballoutcode/.ime.FreekeyboardImeService`; live keyboard shown in Messages compose field. `agent4-01…03`. |
| 2 | EN/RU switch | **PASS** | Globe tap EN→RU→EN; typed `Йцу` (RU, auto-cap) then `Hi` (EN) appended in same field, both scripts committed correctly. `agent4-04…06`. |
| 3 | LP Space → in-IME LanguagePicker (not system Popup) | **PASS** (not flaky this run) | `adb shell input swipe <space> <space> 1200` (same-point hold) opened **"Сменить язык"** sheet in-IME (not Compose `Popup`); `dumpsys window windows` confirms the window's `package=com.freekeyboardballoutcode`, i.e. owned by the IME app itself, not the host app or system. Closed cleanly via "Закрыть". `agent4-08-space-lp-attempt1.png`, `agent4-09-lp-closed.png`, `agent4-lp-ui1.xml`. |
| 4 | Space caret swipe L/R; **no** glide typing | **PASS** | Typed `asdf`, swiped space → caret jumped to **start** of field, no stray space inserted (`WAsdf` after inserting marker at new caret pos). Swiped across the entire top letter row (q→p) → only **one incidental digit** (`1`, a corner-hint artifact of the touch-down key) committed, **no** multi-letter glide word. `agent4-10…12`. |
| 5 | `?123` strip chrome (empty SuggestionBar present) | **PASS** | SYMBOLS layer shows the empty two-divider SuggestionBar chrome (no chips) above the digit/punct rows; Shift→alt-symbols page (`~ ` | • √ π ÷ × ¶ △ …`) and ABC-return both work. `agent4-13-symbols-strip.png`, `agent4-14-symbols-alt.png`. |
| 6 | Suggestions on letters + suppress password/email/URI | **PASS** | Chrome text field: typed `Hel` → chips `Hello/Help/Her`; tapped `Hello` → committed with trailing space + next-word chips `there/how/I`. Chrome password field: **no** freekeyboard strip (only Chrome's native autofill icon row). Chrome email field: same suppression. Chrome omnibox (URI): keyboard goes straight to keys, no strip at all. `agent4-16…27`. |
| 7 | Number / PIN / phone pads | **PASS** | New `agent4-numpad-test.html`. `type=number step=0.01` → decimal pad (`1-9, ., 0, ⌫, ?123, →`). `type=tel` → phone dial pad (`1-9, *, 0+, #, ABC/DEF hints, ⌫, ?123, →`). `type=password inputmode=numeric` → compact PIN pad (`1-9, 0, ⌫, ?123, →`, no letters/decimal/symbols). All three `NumberPad` variants render and focus correctly. `agent4-51.png` (decimal pad), `agent4-52-tel-pad.png`, `agent4-53-pin-pad.png`. |
| 8 | Emoji / Features menu / one-hand / clipboard | **PASS** | Emoji key pref was already **ON** by default this build (unlike Agent T's run) — no manual enable needed. Features grid (`⊞`) opens **Функции**: Тема / Эмодзи / one-hand-toggle / Буфер / Настройки / Закрыть. Emoji panel: search box, Недавние/Смайлы tabs, recent chips, tap-to-insert (confirmed `😘` committed into message text), ABC-return. One-hand mode (enabled via Settings "Режим одной руки"): keyboard docks to **~68% width** with a rail (↔ flip side, ⤢ expand); flip moved the dock from right→left correctly; expand restored full width. Clipboard panel ("Буфер обмена"): shows 3 recent clipboard entries with "Закрепить" (pin) actions. `agent4-60…69`. |
| 9 | Dark theme + landscape | **PASS** | Dark theme active throughout (Settings shows "Тёмная" selected; charcoal keyboard in every screenshot). Landscape (`user_rotation=1`): wide keyboard, no fullscreen "extract" mode (compose field stays visible above keys), full bottom chrome present, language name on space still shown. `agent4-70-landscape.png`. |
| 10 | User-sim: EN+RU + space-swipe caret + emoji + `@`/`.` + Enter | **PASS** | End-to-end: typed `Test` (EN, auto-cap) + space → globe→RU → typed `привет` (RU suggestions `приветик/приветств…/привета` shown live) → space-swiped caret to **start** of `привет` → switched to `?123`, inserted `@` at that exact caret position, returned to ABC (still RU) → swiped caret to **end** → inserted `.` via main period key → tapped Send. Sent message bubble reads exactly **`Test @привет .`** — confirms mixed-script commit, caret-swipe positioning survives a layer switch, and `IME_ACTION_SEND` fires correctly. `agent4-73…79`. |

**Counts**: **10/10 PASS**, 0 FAIL, 0 FLAKY (scenario 3, flagged as flaky-risk by the brief, actually
passed cleanly this run — the `swipe`-as-hold trick reliably opens the LanguagePicker).

## Notable positive findings (regressions fixed since Agent T)

- **One-hand mode** (`A11-002`) — Agent T recorded a **FAIL** (no visible dock). This run confirms the
  `0.68f` dock + rail (flip + expand) **works correctly** live on-device. Regression resolved.
- **Space LP → LanguagePicker** (`A07-003`/`FK-LP-01`) — Agent T recorded a **FAIL** (adb couldn't
  drive it). This run's `input swipe <p> <p> 1200` (same-point long hold) reliably opens it, and window
  ownership confirms it's the in-IME sheet, not a system Popup. Regression resolved / re-verified.
- RU suggestion/next-word dictionary quality is good in practice (`привет` → `приветик/приветствую/
  привета`; `как/что/я` as next-word after `привет`), consistent with `agent2b` "Done" claims.

## Bugs filed

See `agent4-bugs.md` for full repro steps. Summary: **1 Medium, 1 Low**, 0 Blocker/High (none found;
nothing required an in-cycle fix).

## IME state at hand-off

- `com.freekeyboardballoutcode/.ime.FreekeyboardImeService` remains **enabled and selected as default**
  IME (`settings get secure default_input_method` confirmed after every scenario, including after the
  emulator restart and the final settings cleanup pass).
- Preferences left as found except: one-hand mode was toggled ON for scenario 8 testing and explicitly
  toggled back **OFF** afterward to restore the default state. EN + RU both remain enabled. Emoji key
  pref was already ON (default) and left unchanged. Theme remains "Тёмная" (unchanged, was already set
  by a prior agent).
- No outstanding dialogs, panels, or non-default layers left open on the keyboard.
