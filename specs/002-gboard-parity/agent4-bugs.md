# Agent 4 — Bug log (Gboard parity QA pass)

**Date**: 2026-11-07
**Device**: `emulator-5554` (NoJunkie `Medium_Phone`), 720×1280 @ 320dpi
**APK**: debug build already installed by Agent 3b (unchanged this session)
**Severity counts**: Blocker **0** · High **0** · Medium **0** (was 1, fixed) · Low **0** (was 1, fixed)

High: none filed. Medium-1 and Low-1 fixed in follow-up (prefs re-sync on IME show + static Features label).

---

## MEDIUM-1 — Settings "Режим одной руки" OFF does not disable an already-active one-hand session
**Status**: **FIXED** — `FreekeyboardImeService.syncPreferencesFromStore()` on `onStartInputView` / `onWindowShown`; Features toggle persists side/enabled immediately; unit test `applyPreferences_overridesRuntimeOneHandedMode`.

**Area**: Features / one-hand mode (`A11-002`) — `UserPreferencesRepository` one-hand pref vs. the
Features-menu runtime toggle in `KeyboardRoot`/`FeaturesMenu`.

**Symptom**: Turning the *Settings* app's "Режим одной руки" switch **OFF** while the keyboard already
has one-hand mode active (turned on earlier via the in-keyboard Features-menu button) does **not**
restore the full-width keyboard. The live IME session keeps rendering the ~68% one-hand dock + rail
indefinitely, even after:
- Navigating away from and back into the Settings screen (switch visually shows OFF).
- Returning to the host app and reopening the keyboard (new `onStartInputView` call on a different
  field).

The **only** way found to exit one-hand mode once the Settings toggle has drifted out of sync is the
in-keyboard Features-menu button itself (which does work correctly and instantly restores full width).

**Why this matters**: A user who discovers one-hand mode via the keyboard's own Features menu, then
later tries to permanently disable it from the Settings screen (the more "discoverable" long-term
control for most users), will conclude the setting is broken, since the keyboard keeps docking to one
side no matter how many times they flip the Settings switch.

**Suspected root cause** (not confirmed in code, inferred from behavior): the Features-menu one-hand
button and the persisted `UserPreferencesRepository` pref appear to be two separate state sources —
one a per-session mutable toggle inside the already-composed `KeyboardRoot`, the other a `DataStore`
flow collected only when the input view is first (re)created. Toggling the pref after the view is
already composed with one-hand `true` from a runtime toggle doesn't re-collect/override it.

**Repro**:
1. `adb shell input keyevent KEYCODE_HOME`; launch any app with a text field (e.g. Messages compose).
2. Tap the field → keyboard appears full-width.
3. Tap the Features grid icon (⊞, top-left of the suggestion strip) → tap the one-hand tile (shows
   "Одной рукой" when inactive) → keyboard docks to ~68% width with the flip/expand rail. **(one-hand
   now ON, via runtime toggle only — Settings pref untouched.)**
4. Open `com.freekeyboardballoutcode/.settings.SettingsActivity`, scroll to "Режим одной руки", confirm
   it is OFF (it was never turned on there) — note it already doesn't reflect the runtime-ON state
   (a related sync gap: the Settings switch doesn't read back the live toggle either).
5. Explicitly tap the Settings switch ON then OFF again (to rule out "never was on"), return to the
   host app, reopen the same field.
6. **Observed**: keyboard still docks to one-hand width. Expected: full width, since the persisted pref
   is OFF.
7. Tap the Features-menu one-hand tile again (now labeled "Обычный") → full width restores correctly.

**Evidence**: `specs/002-gboard-parity/evidence/agent4-85-onehand-toggle-final.png` (Settings switch
toggled OFF), `agent4-87-verify-kb.png` (new conversation field, keyboard still one-hand-docked after
the Settings toggle was OFF and the app was restarted), `agent4-88-features-check.png` (Features menu
still shows the active-state label "Обычный", confirming the runtime toggle, not the Settings pref, is
the source of truth), `agent4-89-toggle-via-menu.png` (Features-menu toggle *does* work, restoring full
width).

**Suggested fix direction**: ensure the one-hand Compose state in `KeyboardRoot` is a single source of
truth derived from (or two-way bound to) the persisted preference — either have the Features-menu
toggle write through to `UserPreferencesRepository` immediately (so Settings reflects it), and have the
keyboard's one-hand state re-collect the DataStore flow on every `onStartInputView`/field change rather
than caching it only once per process lifetime.

---

## LOW-1 — Features-menu one-hand tile label flips between "Одной рукой" and "Обычный" with no visual state indicator besides text
**Status**: **FIXED** — static label «Одной рукой»; active = filled chip + «✓» (`FeaturesMenu.kt`).

**Area**: `FeaturesMenu` one-hand tile copy/affordance.

**Symptom**: Every other Features-menu tile shows a static feature name (`Тема`, `Эмодзи`, `Буфер`,
`Настройки`, `Закрыть`). The one-hand tile instead shows the *action it will perform next*
("Одной рукой" when inactive, "Обычный" when active) with no other visual cue (no checkmark, no
highlighted border distinct from the other tiles' resting state, no icon change). This is consistent
with a mute/unmute-style toggle pattern and is *not* a functional bug (it does correctly reflect state —
see MEDIUM-1 above for the one confirmed case where it's the *only* correct indicator of true state),
but it's the only tile in the grid that behaves this way, which is a minor, easy-to-miss inconsistency
for a user scanning the grid quickly.

**Repro**: Open Features menu with one-hand mode inactive → tile reads "Одной рукой". Tap it → tile
grid re-renders (same menu) → tile now reads "Обычный". No other visual difference from the resting
tile style.

**Evidence**: `agent4-62-features-menu.png` (inactive state not separately captured — this run started
from an already-active one-hand session; the *active*-state label "Обычный" is visible in
`agent4-62-features-menu.png`, `agent4-88-features-check.png`).

**Suggested fix direction**: cosmetic only — e.g. keep a static "Одной рукой" label and add a small
active-state indicator (checkmark/dot/filled background) consistent with how other stateful prefs are
shown elsewhere in the app, so the grid's visual language stays consistent. Low priority; does not
block parity.

---

## Non-bugs / clarifications (for the record, not filed as defects)

- **Chrome `type=number` / `type=tel` fields "not focusing" in early testing**: traced to a QA-script
  coordinate-scaling mistake (screenshot preview vs. actual 720×1280 device pixels), not a freekeyboard
  defect. Re-tested with corrected coordinates — all three `NumberPad` variants (decimal, phone, PIN)
  focus and render correctly on the first tap. See `agent4-qa-report.md` methodology note.
- **Password field briefly shows the typed character in plaintext before masking** (Chrome
  `agentT-field-test.html` / `agent4-numpad-test.html` PIN field): this is the host `TextView`/WebView's
  own "flash last character" behavior for *any* IME on stock Android, not something freekeyboard
  controls. Not filed.
- **Emulator process (`emulator.exe`) exited unexpectedly mid-session**: host/emulator infra flake,
  unrelated to any freekeyboard action; relaunched per the `emulator-nojunkie` skill and resumed with
  no data loss. Not a product bug; documented in `agent4-qa-report.md` for the record.
