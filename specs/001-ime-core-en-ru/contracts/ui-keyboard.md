# Contract: Keyboard UI Behavior

**As-built**: 2026-11-08

## Layouts

- EN letters: QWERTY arrangement comparable to contemporary Gboard
- RU letters: ЙЦУКЕН arrangement comparable to contemporary Gboard (11-key top row)
- Symbols layer: digits and common punctuation; Shift opens alternate symbols page
- Number / phone / datetime: dedicated `KeyboardLayer.NUMBER` digit pad (feature 002)
- Optional number row: `1…0` above letter rows when `number_row` preference is true
  - EN: 10 digit keys
  - RU: 10 digit keys + inert `NOOP` pad (`num_pad`) so columns align with 11 letter keys
  - Password fields MAY show number row when `number_row_for_passwords` is true

## Controls

| Control | Behavior |
|---------|----------|
| Globe (`LANGUAGE`) | Short tap toggles between enabled languages when ≥2 enabled; hidden when fewer than two; long-press opens language picker (same as Space) |
| Space | Commits space; label shows active language Russian name (`Английский` / `Русский`); no autocorrect/period when suggestions suppressed |
| Space long-press | Language picker («Сменить язык») listing enabled languages; footer «Настройки языка» |
| Space horizontal swipe | Moves caret by code-point steps; DPAD fallback when buffer truncated or password/suppress |
| Shift (letters) | Tap: `OFF`→`ONCE`, `ONCE`→`OFF`, `CAPS`→`OFF`. Double-tap (~400 ms) or long-press: latch `CAPS` until single tap unlocks |
| Shift (symbols) | Tap **or** long-press toggles symbols ↔ symbols_alt (long-press MUST NOT be a no-op) |
| `NOOP` pad | No commit, no Shift consume, no haptic/sound feedback |
| Long-press key with variants | Show popup when long-press enabled; selection commits variant |
| Backspace hold | Repeat delete; after ~900 ms hold, delete by word (whitespace run + preceding token, code-point safe; buffer ≤1024) |
| Emoji key | Opens emoji panel (omitted when `show_emoji_key` is false) |
| ABC from emoji/symbols/number | Returns to letters with current language; emoji ABC is a 48dp bottom control |
| Suggestion strip | Up to 3 offline candidates on LETTERS when not suppressed; Features toolbar (⊞) remains on suppressed fields (toolbar-only); inline autofill chips may appear (003) |
| Features menu | Theme cycle, emoji, one-hand, clipboard (when enabled & not password/suppress/incognito), settings |
| Press popup | Circular preview above letter keys when `popup_on_keypress` is true |
| Key borders | 1dp outlines when `key_borders` is true |
| Accessibility | TalkBack activates tap via semantics `onClick`; long-press via `onLongClick` when present; backspace exposes “Delete previous word” custom action |

## Themes

| Theme | Visual obligation |
|-------|-------------------|
| Light | Soft blue-grey bed, white letter keys, cool grey modifiers |
| Dark | Neutral charcoal bed/keys (no blue/cool slate); elevated grey key faces |
| OLED Dark | Pure black primary background, near-black keys |

Settings Activity MUST render with the same theme preference (background/surface/system bars). Features theme cycle updates IME nav chrome immediately and persists without losing to a stale DataStore sync.

## Orientation / Chrome

- Portrait and landscape: bottom IME; keys remain hittable
- `onEvaluateFullscreenMode` = false
- Content/visible insets align to keyboard top; touchable insets = FRAME
- Bottom padding reserves system IME nav strip so keys are not covered
- Screen-bottom → spacebar gap matches contemporary Gboard (~54 dp / ~108 px @ 320 dpi)
- Portrait letter metrics (320 dpi reference): suggestion 52 dp; key face 40.5 dp; row gap 10 dp; landscape uses shorter keys / tighter gaps via `GboardMetrics`
- Emoji panel height = `letterLayerContentHeight(landscape, numberRow, includeSuggestionStrip)`
- First-frame inset estimate assumes strip chrome present; posts `updateInputViewShown` when height becomes non-zero
- IME nav chrome tint: prefer framework nav-frame class-name hints; fallback to bottom full-width strip ≈ nav height

## Branding

- No Google/Gboard marks in UI, icons, or About
- Settings and keyboard chrome strings in Russian
