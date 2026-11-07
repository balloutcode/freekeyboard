# Gboard public docs digest

Synthesized for freekeyboard parity. Prefer official Help; secondaries fill settings-tree gaps Google Help omits.

## Primary sources (official)

| Topic | URL | Takeaways for parity |
|-------|-----|----------------------|
| Use your keyboard | https://support.google.com/gboard/answer/2842292 | Spacebar **swipe L/R** moves cursor; **double-tap Shift** = Caps Lock; **touch & hold** key = accents/options; emoji + GIF; language add; **hold Space** to switch languages; show language-switch key under Preferences (XOR with emoji key tip); handwriting (OOS) |
| Set up Gboard | https://support.google.com/gboard/answer/6380730 | Features menu → More Settings; Glide/Text correction/Voice; languages & layouts; Search button (OOS) |
| Theme / sound / vibration | https://support.google.com/gboard/answer/6102154 | Theme from toolbar; Preferences → Key tap: Sound, Volume, Keyboard vibration; emoji size under Emojis/stickers/GIFs; device vibration override |
| Suggestions / corrections | https://support.google.com/gboard/answer/7068415 | Suggestion strip; long-press suggestion → Trash; Proofread/Fix it (OOS/cloud); Add to dictionary; Text correction options; Smart Compose (OOS) |
| Fix problems / personalization | https://support.google.com/gboard/answer/9058584 | Advanced → Personalization off; Text correction toggles; Show suggestion strip; Dictionary delete learned words; privacy of typed text |
| Learn how Gboard gets better | https://support.google.com/gboard/answer/12373137 | Privacy: Personalize for you / Improve for everyone / Delete learned; audio donations (all OOS for freekeyboard) |

## Secondary sources (settings inventory fill)

| Topic | URL | Use |
|-------|-----|-----|
| Preferences deep dive | https://www.pocket-lint.com/what-is-gboard-tips-tricks/ | Number row; emoji vs language key XOR; one-handed; long-press symbols; show app icon |
| Number row / symbols hold | https://www.androidauthority.com/gboard-settings-i-always-change-3723138/ | Number row default off; password number row; touch & hold for symbols |
| Clipboard / one-handed / emoji key | https://maketecheasier.com/master-google-gboard-for-android/ | Clipboard enable/pin/1h; floating/one-handed; emoji switch key |
| Resize vs height | https://9to5google.com/2023/10/05/gboard-keyboard-height-resize/ | Height presets removed; Resize via toolbar |
| Tips (space cursor, Caps, hold) | https://rottenwifi.com/get-the-most-out-of-gboard-tips-and-tricks-for-androids-best-all-round-keyboard/ | Confirms Help behaviors |
| Engadget comfort prefs | https://www.engadget.com/2305040/how-to-use-gboard-boost-productivity-android-phone/ | Resize/float/one-hand; touch & hold duration; vibration strength; comma/period keys |

## Emulator package evidence

- Device: emulator-5554, Android 15, 720×1280@320dpi  
- Package: `com.google.android.inputmethod.latin` **18.5.6.993608717-release-x86_64**  
- Details: `evidence/emulator-session.md`

## Behavioral digest (in-scope relevant)

### Typing chrome
- Letter layouts language-specific; Space often labeled with language name.
- `?123` → digits/punctuation; Shift/`=\<` → alt symbols; ABC → letters.
- Suggestion/toolbar strip chrome stays on `?123` (word chips usually empty).
- Optional always-on **number row**; when on, digit hints leave letter keys.
- Long-press letters/symbols for variants; delay configurable.

### Number / phone / PIN fields (InputType)
- `TYPE_CLASS_NUMBER` → compact numeric keypad (0–9), not QWERTY.
- `TYPE_NUMBER_VARIATION_PASSWORD` (`numberPassword`) → PIN-style digit pad (masked by host); no ABC/Space row.
- `TYPE_NUMBER_FLAG_DECIMAL` / `TYPE_NUMBER_FLAG_SIGNED` → `.` / `−` keys.
- `TYPE_CLASS_PHONE` → dial pad with letter hints and `*` `#`.
- Suggestion strip suppressed for non-TEXT classes (platform norm).

### Shift
- Single tap: one-shot capital (or unlock Caps).
- Double-tap: Caps Lock.
- Long-press Shift: Caps (widely observed; freekeyboard matches).
- On symbols: Shift toggles alt page.

### Language
- Hold Space → switch/picker among enabled keyboards.
- Optional globe/language key (Preferences); tip: may require disabling emoji switch key.
- Does not change system locale.

### Editing helpers
- Spacebar horizontal swipe → move cursor (Help-documented).
- Backspace: char repeat; extended hold → word delete (common Android IME; freekeyboard implements).
- Double-space → period (Text correction / Preferences family).
- Auto-capitalization after sentence start.

### Suggestions
- Strip above keys; tap accept; long-press reject to trash (Gboard).
- Next-word / emoji suggestions configurable under Text correction.
- Hidden for passwords / no-suggestions fields (platform expectation).

### Panels / toolbar
- Features menu (grid): theme, clipboard, one-handed, resize, emoji, GIF, voice, translate, floating — **most OOS**.
- Offline-feasible subset: emoji panel, local clipboard, one-handed, resize, theme.

### Themes / feedback
- Built-in themes + custom backgrounds (photo themes = brand/asset risk).
- Sound + vibration strength; popup on keypress preference family.

## Gaps in official docs

Google Help does **not** publish a complete settings dump. Inventory in `gboard-settings-inventory.md` merges Help + secondary guides + emulator package version and notes remaining gaps explicitly.
