# Quickstart Validation: IME Core EN/RU

## Prerequisites

- Android Studio / SDK with compileSdk 37
- Emulator or phone: Android 12+ (API 31+)
- Built debug APK of `com.freekeyboardballoutcode`
- Soft keyboard visible with hardware keyboard: `adb shell settings put secure show_ime_with_hard_keyboard 1`

## Setup

1. Assemble and install: `./gradlew :app:installDebug`
2. Enable IME: `adb shell ime enable com.freekeyboardballoutcode/.ime.FreekeyboardImeService`
3. Select IME: `adb shell ime set com.freekeyboardballoutcode/.ime.FreekeyboardImeService`
4. Or open freekeyboard settings → «Включить клавиатуру в системе» / «Выбрать freekeyboard»
5. Open Messages compose (`adb shell am start -a android.intent.action.SENDTO -d sms:5550100`) and focus the text field (tap the EditText, not the left “+” attachment control)

## Scenarios

### S1 — Basic typing + language (P1)

1. Focus a text field (verify `mInputShown=true`)
2. Type `Hello` on EN (auto-cap may capitalize the first letter)
3. Confirm spacebar shows `Английский`
4. Tap globe → RU (spacebar shows `Русский`)
5. Type `да`
6. Long-press Space → picker → select Английский

**Expect**: Editor contains mixed Latin + Cyrillic; layout and spacebar label stay in sync.

### S2 — Symbols, shift / Caps Lock, long-press, number row

1. Open ?123; confirm digits/punctuation; tap Shift for alt symbols; **long-press** Shift also toggles alt; ABC back
2. After a lowercase context (e.g. type `a` then Space), **double-tap** Shift → Caps Lock icon; type `ui` → `UI`; tap Shift once → type `ui` → `ui`
3. Long-press Shift → Caps Lock latches; type capitals; tap Shift to unlock
4. Long-press `e` and pick an accent (e.g. `è`)
5. In settings, toggle «Ряд с цифрами» on/off and confirm digit row vs letter hints
6. On RU + number row: confirm 11th pad slot does nothing (no insert, Shift ONCE stays)

**Expect**: Digits/punctuation, Gboard-like Caps Lock, accents, and number-row preference behave as specified.

### S3 — Landscape

1. Rotate device to landscape while keyboard visible
2. Continue typing

**Expect**: Bottom soft keyboard (not fullscreen extract); keys usable.

### S4 — Emoji

1. Open emoji panel; browse categories; pick an emoji
2. Tap bottom **ABC** (large hit target) to return to letters — keyboard height should not jump
3. Reopen emoji → Недавние contains the picked emoji
4. Optional: enable number row and/or rotate to landscape; reopen emoji — height still matches letter layer

**Expect**: Unicode emoji inserted; previous language layout restored; no height jump.

### S5 — Themes & preferences (Russian settings)

1. Open freekeyboard settings; verify Russian UI
2. Cycle Светлая / Тёмная / OLED чёрная — confirm **settings screen** and **keyboard** both change (Dark = grey charcoal, OLED = pure black)
3. Toggle borders, sound/haptic/popup, suggestions, auto-cap, double-space, emoji key

**Expect**: Theme and toggles persist across hide/show keyboard; settings chrome matches the selected theme.

### S6 — Suggestions & double-space

1. Type a partial word; tap a suggestion
2. Type a letter, Space, Space with double-space enabled
3. Focus a password or email field — suggestion strip stays hidden

**Expect**: Candidate acceptance inserts word + space; double-space yields `. `; strip suppressed on password/email.

### S7 — Human-emulation smoke

Simulate a short bilingual message: `Hello` → globe → `да` → emoji → return ABC.

**Expect**: `Hello да 🙂` (or similar) with no stuck emoji panel.

### S8 — Layer reset on new field

1. Open ?123 (or emoji) in Messages
2. Switch focus to another text field / restart input

**Expect**: Keyboard opens on letters (not stuck on symbols/emoji); language unchanged.

### S9 — 002 parity: RU, number pad, toolbar (manual)

1. Tap space inserts ` `; swipe L/R on space moves caret; long-press space opens language picker
2. RU: long-press `е` → pick `ё`; type `прив` → suggestion includes `привет`
3. Focus a number/phone field → digit pad; ABC returns to letters
4. Suggestion strip apps button → Функции: theme cycle, emoji, one-hand, clipboard, settings (no mic/search)
5. One-hand: enable → keyboard shrinks with side rail; flip ↔; expand ⤢ exits
6. Copy text in another app → open Буфер → tap paste; pin survives; unpinned TTL ~1h
7. Long-press an unwanted suggestion chip → it disappears from future suggestions
8. Settings: enable «Ряд цифр в паролях»; focus password → number row appears even if global row off

**Expect**: Behaviors above; still no glide typing / voice / GIF / Google branding.

## Out of scope checks (must NOT appear)

- Swipe path drawing / gesture typing
- Voice input / translate / GIF / stickers / floating keyboard
- Gboard or Google branding
- Network requirement for core typing, emoji, themes, or suggestions
