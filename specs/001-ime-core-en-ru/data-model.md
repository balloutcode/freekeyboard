# Data Model: IME Core EN/RU

**As-built**: 2026-11-08

## Entities

### InputLanguage

| Field | Type | Rules |
|-------|------|-------|
| id | enum: `EN`, `RU` | Fixed set for 001 |
| enabled | boolean | At least one MUST be true; default both true |
| displayNameRu | string | Russian label for settings/picker/spacebar (`Английский`, `Русский`) |
| tag | string | `en-US` / `ru-RU` for subtype metadata |

**Transitions**: User toggles enabled in settings; cannot disable the last remaining language. Globe key visible only when `enabledLanguages.size >= 2`.

### KeyboardLayer

| Field | Type | Rules |
|-------|------|-------|
| id | enum: `LETTERS`, `SYMBOLS`, `EMOJI`, `NUMBER` | Mutually exclusive visible layer |
| symbolsAlt | boolean | When `SYMBOLS`, toggles primary vs alternate symbol page via Shift (tap **or** long-press) |

**Transitions**: LETTERS ↔ SYMBOLS via layer keys; LETTERS/SYMBOLS → EMOJI via emoji key; EMOJI/NUMBER → LETTERS via ABC; number/phone/datetime start on `NUMBER`. **`onStartInput`** forces LETTERS or NUMBER, `symbolsAlt=false`, dismiss panels (language preserved). `onStartInputView` does not re-reset layer.

### ShiftState

| Field | Type | Rules |
|-------|------|-------|
| mode | enum: `OFF`, `ONCE`, `CAPS` | Gboard-like Shift on letter layer (see transitions) |

**Transitions** (letters):
- Tap: `OFF` → `ONCE`; `ONCE` → `OFF` (or `CAPS` if second tap within ~400 ms double-tap window); `CAPS` → `OFF`
- Long-press Shift: → `CAPS`
- Character commit from `ONCE` → `OFF` (unless auto-cap immediately re-asserts `ONCE`); `CAPS` stays until dismissed
- On SYMBOLS, Shift tap **or** long-press toggles `symbolsAlt` instead of letter case
- Empty/`NOOP` keys MUST NOT consume `ONCE`

### ThemePreference

| Field | Type | Rules |
|-------|------|-------|
| theme | enum: `LIGHT`, `DARK`, `OLED_DARK` | Exactly one; default `LIGHT` |

**Transitions**: User selects theme in settings; applied to keyboard Compose theme, IME nav chrome tint, and the settings Activity Material3 surfaces / system bars. Dark = neutral charcoal (`#1A1A1A` bed); OLED = `#000000`.

### KeyDefinition

| Field | Type | Rules |
|-------|------|-------|
| code | string | Stable key id within layout |
| label | string | Visible glyph(s); space label overridden at render by language display name |
| output | string | Primary commit text (empty for `NOOP`) |
| hint | string? | Small corner hint (often digit); cleared when number row is on |
| variants | list of string | Long-press alternatives; may be empty; cleared when long-press disabled |
| action | enum | `COMMIT`, `NOOP`, `SHIFT`, `BACKSPACE`, `SPACE`, `ENTER`, `LAYER_SYMBOLS`, `LAYER_LETTERS`, `EMOJI`, `LANGUAGE` |
| weight | float | Relative width in row |
| isSpecial | boolean | Modifier chrome styling |

**`NOOP`**: Inert spacer (RU number-row 11th pad). No commit, no Shift consume, no key feedback.

### RecentEmoji

| Field | Type | Rules |
|-------|------|-------|
| emoji | string | Single emoji grapheme / Unicode sequence |
| order | list | Most-recent-first |
| maxItems | int | Cap 32; oldest dropped |

**Transitions**: On successful emoji insert, upsert and trim list (DataStore + in-memory state).

### SuggestionCandidate

| Field | Type | Rules |
|-------|------|-------|
| text | string | Completion or next-word candidate |
| source | enum | `COMPLETION`, `NEXT_WORD` |
| limit | int | Strip shows up to 3 |

**Rules**: Offline dictionary/fuzzy match; optional next-word / autocorrect; optional offensive filter. **Word chips** hidden when `show_suggestion_strip` is false, layer ≠ LETTERS, or `suppressSuggestions`. **Features toolbar** still mounts when suppressed. Rejected words persist only outside suppressed/password/incognito fields (store `rejected_suggestions`).

### ClipboardEntry (feature 002/004)

| Field | Type | Rules |
|-------|------|-------|
| id | string | `${epochMs}_${UUID}` |
| text | string | ≤500 chars |
| pinned | boolean | pin refreshes clock; TTL 7d pinned / 1h unpinned |
| createdAtMs | long | prune persisted via `prunePersisted()` |

Store name: `clipboard_history` (excluded from backup). Default preference **off**.

### UserPreferences (aggregate)

Persisted DataStore document (`user_prefs`) — excluded from Android backup:

| Key | Type | Default |
|-----|------|---------|
| theme | string enum name | `LIGHT` |
| key_borders | boolean | `true` |
| enabled_languages | set | `{EN, RU}` |
| last_language | enum | first enabled |
| recent_emoji | delimited string | empty |
| number_row | boolean | `false` |
| show_emoji_key | boolean | `true` |
| sound_on_keypress | boolean | `false` |
| haptic_on_keypress | boolean | `true` |
| popup_on_keypress | boolean | `true` |
| long_press_for_symbols | boolean | `true` |
| long_press_delay_ms | int | `300` (100–700) |
| show_suggestion_strip | boolean | `true` |
| next_word_suggestions | boolean | `true` |
| block_offensive_words | boolean | `true` |
| auto_capitalization | boolean | `true` |
| double_space_period | boolean | `true` |
| number_row_for_passwords | boolean | `false` |
| one_handed_mode | boolean | `false` |
| one_handed_side | enum | `RIGHT` |
| clipboard_history_enabled | boolean | `false` (opt-in) |
| sound_volume / haptic_strength / emoji_font_scale | float | `1` / `1` / `1` |
| autocorrect_enabled | boolean | `false` |
| personal_dictionary | string set | empty |

## Validation Rules

- `enabledLanguages` non-empty subset of {EN, RU}.
- `lastActiveLanguage` MUST be in `enabledLanguages` (if not, fall back to preferred/first enabled).
- OLED theme implies pure-black background tokens in theme mapping.
- When `number_row` is true, layout gains a digit row and letter digit hints/variants are stripped.
- EN number row = 10 digit keys; RU = 10 digits + 1 `NOOP` pad (aligns to 11-key top letter row).
- When `show_emoji_key` is false, `EMOJI` action keys are removed from layouts.
- When fewer than two languages are enabled, `LANGUAGE` action keys are removed.

## Layout metrics (shared)

`GboardMetrics.letterLayerContentHeight(landscape, numberRow, includeSuggestionStrip)` is the single source for:
- Emoji panel fixed height (avoid jump vs letter layer)
- First-frame `onComputeInsets` estimate when Compose height is still 0

## State Diagrams

### Layer

```text
LETTERS --?123--> SYMBOLS --ABC--> LETTERS
LETTERS --emoji--> EMOJI --ABC--> LETTERS
SYMBOLS --emoji--> EMOJI --ABC--> LETTERS
SYMBOLS --Shift/long-Shift--> SYMBOLS(alt) --Shift/long-Shift--> SYMBOLS
NUMBER --ABC--> LETTERS
* --onStartInput--> LETTERS | NUMBER (shift OFF, panels dismissed)
```

### Language

```text
EN --globe--> RU --globe--> EN
* --long-press Space--> LanguagePicker --select--> EN|RU
```
