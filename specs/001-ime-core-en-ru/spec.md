# Feature Specification: IME Core EN/RU

**Feature Branch**: `001-ime-core-en-ru`

**Created**: 2026-11-06

**Status**: Implemented (as-built sync 2026-11-07)

**Input**: System soft keyboard freekeyboard with Gboard-like UX (no Google brand): IME enablement, EN QWERTY + RU ЙЦУКЕН, language switch, symbols/digits, long-press variants, emoji panel, offline suggestions, Light/Dark/OLED themes, Russian settings UI, portrait + landscape; no swipe-typing; no Google cloud/voice/translate/floating keyboard.

## Clarifications

### Session 2026-11-06 (initial)

- **Language switch**: Globe-style language key toggles among enabled languages when two or more are enabled (v1: EN↔RU); long-press Space opens an explicit language picker. No swipe-to-switch and no swipe-typing.
- **Landscape**: Soft keyboard stays bottom-docked; fullscreen extract IME mode is disabled for phone use.
- **Emoji panel**: Full offline panel with category tabs, recents, Unicode insertion; ABC return on the bottom bar; no GIF/sticker tabs; no network emoji packs.
- **Themes**: Exactly three user-selectable themes — Light, Dark, OLED Dark. No “follow system” option; selection is sticky.
- **Number row**: Optional always-visible digit row (settings toggle). When enabled, digit hints are cleared from letter keys; when disabled, digits remain reachable via symbols layer and long-press hints.

### Session 2026-11-06 (post-implementation / maintenance)

- **Suggestions**: Offline candidate strip (completions + next-word) is in scope for the shipped IME core, with toggles for strip visibility, next-word, and offensive-word blocking. No cloud personalization.
- **Spacebar label**: Shows the active input language Russian display name (`Английский` / `Русский`), matching Gboard’s language-on-space pattern under freekeyboard RU UI.
- **Key feedback**: Sound, haptic, and press-popup are user-configurable; long-press delay is adjustable (100–700 ms).
- **Text helpers**: Auto-capitalization and double-space → period are user-configurable.
- **Key borders**: Optional key outlines for Light/Dark/OLED key chrome.

### Session 2026-11-06 (QA + Caps / chrome parity)

- **Caps Lock (Gboard parity)**: Single tap Shift = one capital (`ONCE`); double-tap Shift within ~400 ms = Caps Lock (`CAPS`); long-press Shift also latches `CAPS`. Tap Shift while latched = unlock. CAPS persists across letter commits until dismissed. On SYMBOLS, Shift (tap or long-press) toggles the alternate symbols page instead of letter case (see also Session 2026-11-07).
- **Dark theme**: Standard Dark uses neutral charcoal greys (equal RGB channels; no cool/blue slate like Gboard Material You). OLED Dark remains pure black and is a separate choice.
- **Settings theming**: The settings Activity MUST follow the same Light / Dark / OLED Dark preference as the keyboard (scaffold, surfaces, and system bar styles).
- **IME height / bottom inset**: Portrait letter keyboard bottom padding MUST match contemporary Gboard spacing from the physical screen bottom edge to the spacebar row (~54 dp / ~108 px at 320 dpi), including the system IME nav strip reserve.
- **Functional QA (emulator 720×1280 @ 320 dpi, 2026-11-06)**: Hello + globe → `да`; Caps double-tap/hold; symbols digit; space long-press picker; double-space period; suggestion accept; backspace hold; settings three themes — zero blocker defects.

### Session 2026-11-07 (as-built / post code-review)

- **Symbols Shift long-press**: On the symbols layer, long-press Shift MUST toggle the alternate symbols page the same way a tap does (MUST NOT be a no-op).
- **RU number-row pad**: When the number row is enabled on the 11-key Russian top row, the extra 11th slot is an inert spacer (`NOOP`) — it MUST NOT commit text and MUST NOT consume one-shot Shift.
- **New editor / start input**: Starting input on a field MUST open the letter layer (reset Symbols/Emoji, clear symbols-alt, dismiss language picker, Shift `OFF`); language preference is preserved.
- **TYPE_NULL / raw editors**: Space, digits, common punctuation, and Latin letters MUST be delivered as hardware key down/up events (with Shift meta where needed). Non-mappable glyphs (Cyrillic, emoji) MAY fall back to `commitText`.
- **Suggestion suppression**: Password, `TYPE_NULL`, non-text classes, `NO_SUGGESTIONS`, email, URI, and filter fields MUST hide the suggestion strip.
- **Emoji / first-frame height**: Emoji panel height and first-frame inset estimates MUST match the letter-layer content height for the current orientation, number-row preference, and suggestion-strip visibility (no jump when opening emoji; no under-estimate when number row is on).
- **Word backspace**: Hold-backspace word delete removes the trailing whitespace run plus the preceding word token, counting Unicode code points.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Enable and Type with Soft Keyboard (Priority: P1)

A user installs freekeyboard, enables it in Android system input settings, selects it as the current input method, opens any text field, and types characters using the on-screen keyboard so text appears in the focused field.

**Why this priority**: Without a working system IME and basic typing, the product has no value.

**Independent Test**: On an Android 12+ phone/emulator, enable freekeyboard, focus a text field, tap letter keys, and confirm characters are inserted. Field remains visible above the keyboard (correct insets).

**Acceptance Scenarios**:

1. **Given** freekeyboard is installed but not enabled, **When** the user opens system Language & input / keyboard settings and enables freekeyboard, **Then** the system lists freekeyboard as an available input method under the freekeyboard brand (not Gboard/Google).
2. **Given** freekeyboard is enabled, **When** the user selects it as the active input method and focuses an editable field, **Then** the soft keyboard appears and can insert text into that field.
3. **Given** the keyboard is visible on a letter layout, **When** the user taps letter keys, Space, Enter, and Backspace, **Then** the editor receives the corresponding insert, space, editor action/newline behavior, and deletions.
4. **Given** a field that only accepts raw key events (`TYPE_NULL`), **When** the user types Latin letters, Space, digits, or common punctuation, **Then** the IME delivers key events (not only `commitText`) and remains stable.

---

### User Story 2 - English and Russian Layouts with Language Switch (Priority: P1)

A bilingual user switches between English QWERTY and Russian ЙЦУКЕН using a globe key and/or long-press Space language picker.

**Why this priority**: Dual-language input is a core product requirement.

**Independent Test**: Type in English, tap globe → Russian, type Cyrillic, long-press Space and pick English explicitly; layouts and spacebar labels match the active language.

**Acceptance Scenarios**:

1. **Given** English is active, **When** the letter keyboard is shown, **Then** keys follow QWERTY Latin placement and the spacebar label reads `Английский`.
2. **Given** Russian is active, **When** the letter keyboard is shown, **Then** keys follow ЙЦУКЕН Cyrillic placement and the spacebar label reads `Русский`.
3. **Given** both languages are enabled, **When** the user taps the globe language key, **Then** the active layout toggles between EN and RU.
4. **Given** the user long-presses Space, **When** the language picker appears, **Then** the user can choose EN or RU explicitly.
5. **Given** a language was last used, **When** the user reopens the keyboard later, **Then** the last active input language is restored.
6. **Given** only one language remains enabled, **When** the keyboard is shown, **Then** the globe key is hidden and long-press Space does not leave the user without a working layout.

---

### User Story 3 - Symbols, Numbers, Shift, Long-Press, and Optional Number Row (Priority: P1)

A user accesses digits/symbols layers, optional number row, Shift/Caps, and long-press variants.

**Why this priority**: Required for everyday typing beyond plain letters.

**Independent Test**: Toggle number row in settings; open ?123; use Shift for symbol alt page; long-press a letter with variants and pick one.

**Acceptance Scenarios**:

1. **Given** the letter keyboard, **When** the user opens the symbols/numbers layer, **Then** digits and common punctuation are reachable without leaving the IME.
2. **Given** the symbols layer, **When** the user taps or long-presses Shift, **Then** an alternate symbols page is shown (toggle); ABC returns to the previous letter language.
3. **Given** a letter key, **When** the user taps Shift then a letter, **Then** the capitalized character is inserted and Shift returns to `OFF` (unless auto-cap re-asserts `ONCE`).
4. **Given** the letter keyboard, **When** the user double-taps Shift (or long-presses Shift), **Then** Caps Lock latches and subsequent letters are uppercase until Shift is tapped once to unlock.
5. **Given** a key that has variants, **When** the user long-presses it, **Then** a popup of alternatives appears and choosing one inserts that character.
6. **Given** number row is enabled, **When** the letter keyboard is shown, **Then** a dedicated `1…0` row appears above letters and digit hints on letter keys are cleared.
7. **Given** number row is enabled on Russian (11-key top row), **When** the number row is shown, **Then** the 11th pad slot is inert (no text commit, no Shift consume).
8. **Given** number row is disabled, **When** long-press for symbols is enabled, **Then** top-row letter keys show digit hints again.

---

### User Story 4 - Portrait and Landscape on Phone (Priority: P1)

A user types in portrait and landscape on a phone-sized device with a usable bottom-docked soft keyboard.

**Why this priority**: Landscape support was explicitly required; fullscreen extract mode would break Gboard-like feel.

**Independent Test**: Rotate while typing; keyboard remains usable without fullscreen extract editor.

**Acceptance Scenarios**:

1. **Given** portrait orientation, **When** the keyboard is shown, **Then** it occupies the bottom soft-input area with Gboard-like key arrangement scaled to width.
2. **Given** landscape orientation, **When** the keyboard is shown, **Then** it remains a bottom soft keyboard (not forced fullscreen extract) and keys remain tappable.
3. **Given** an active cursor position, **When** the user rotates the device, **Then** typing continues without dismissing the IME unexpectedly.

---

### User Story 5 - Emoji Panel (Priority: P2)

A user opens a full emoji panel, browses categories, inserts emoji, and returns via ABC.

**Why this priority**: Required for v1 feature set, but typing letters must work first.

**Independent Test**: Open emoji, pick an emoji, confirm insertion; tap ABC and return to letters; reopen and see recents.

**Acceptance Scenarios**:

1. **Given** the letter keyboard, **When** the user opens the emoji entry point, **Then** a full emoji panel is shown with Russian category tabs and a bottom ABC control.
2. **Given** the emoji panel, **When** the user taps an emoji, **Then** it is inserted into the focused field.
3. **Given** the emoji panel, **When** the user taps ABC, **Then** the previous letter layout (EN or RU) is restored without a visible height jump relative to the letter layer (same orientation / number-row / suggestion-strip configuration).
4. **Given** recent emoji usage, **When** the user reopens the panel, **Then** recently used emoji appear under Недавние.

---

### User Story 6 - Themes, Preferences, and Russian Settings UI (Priority: P2)

A user opens freekeyboard settings (Russian UI), chooses theme and typing preferences, and sees the keyboard reflect those choices.

**Why this priority**: Branding, comfort, and Gboard-like preference coverage without Google services.

**Independent Test**: Change theme / number row / suggestions / feedback toggles; reopen keyboard and verify behavior.

**Acceptance Scenarios**:

1. **Given** freekeyboard settings, **When** the user selects Светлая / Тёмная / OLED чёрная, **Then** the preference persists and both the settings UI and the keyboard use the corresponding appearance (Dark = neutral charcoal; OLED = pure black surfaces).
2. **Given** settings, **When** the user toggles key borders, sound, haptic, popup, long-press, suggestion strip, next-word, offensive blocking, auto-cap, or double-space period, **Then** changes apply on the next keyboard interaction without reinstalling.
3. **Given** settings, **When** the user enables/disables EN or RU, **Then** at least one language remains enabled and the keyboard only offers enabled languages.
4. **Given** a selected theme, **When** the system day/night mode changes, **Then** the keyboard continues to follow the user’s explicit theme choice.

---

### User Story 7 - Offline Suggestions Strip (Priority: P2)

A user sees up to three offline word candidates while typing and can accept a suggestion with a tap.

**Why this priority**: Everyday Gboard-like assistance without cloud services.

**Independent Test**: Type a partial English/Russian word; see completions; tap one to replace the prefix and insert a trailing space; after a word + space, see next-word candidates when enabled.

**Acceptance Scenarios**:

1. **Given** suggestion strip is enabled, **When** the user types a recognizable prefix, **Then** up to three candidates appear.
2. **Given** next-word suggestions are enabled, **When** the cursor follows a completed word and space, **Then** next-word candidates may appear.
3. **Given** offensive blocking is enabled, **When** candidates would include blocked terms, **Then** those terms are filtered or deprioritized.
4. **Given** suggestion strip is disabled, **When** the user types, **Then** no candidate strip is shown.
5. **Given** a password, email, URI, or `NO_SUGGESTIONS` field, **When** the user types, **Then** the suggestion strip is hidden even if the global toggle is on.

---

### Edge Cases

- What happens when freekeyboard is disabled in system settings while an editor is focused? Keyboard must hide; re-enable restores availability.
- How does the system handle password/PIN fields? Keyboard MUST still type; MUST NOT show suggestions; MUST NOT expose characters via insecure overlays beyond normal IME behavior.
- What if the user was on Symbols/Emoji and focuses a new field? IME MUST open on Letters for the new/restarted input session.
- What if only one of EN/RU is enabled? Language switch MUST only cycle/show enabled languages; globe key hidden when fewer than two.
- What if the editor rejects rich content for emoji? IME MUST fall back to inserting emoji as Unicode text.
- What if the device has a hardware keyboard attached? Soft input MAY still show (`show_ime_with_hard_keyboard`); MUST NOT crash.
- What if the user rapidly switches languages and layers? IME MUST remain responsive and not insert the wrong script for the visible layout.
- What if number row makes the IME taller? Insets MUST keep the focused field visible above the keyboard.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Product MUST register as a system input method named freekeyboard with application identity `com.freekeyboardballoutcode`.
- **FR-002**: Users MUST be able to enable freekeyboard in Android input settings and select it as the active IME.
- **FR-003**: Soft keyboard MUST insert text into the focused editor for normal text fields on Android 12+.
- **FR-004**: System MUST provide English QWERTY and Russian ЙЦУКЕН letter layouts with Gboard-like key arrangement for those languages.
- **FR-005**: Users MUST be able to switch between EN and RU without swipe-typing: a globe-style key toggles when two or more languages are enabled, and long-press Space opens an explicit language picker.
- **FR-006**: System MUST remember the last active input language across keyboard sessions.
- **FR-007**: Users MUST access digits and symbol layers (including an alternate symbols page) and return to letters.
- **FR-008**: System MUST support Shift and Caps Lock consistent with contemporary Gboard: tap = one-shot capital; double-tap or long-press Shift = Caps Lock; tap again dismisses Caps. On Symbols, tap or long-press Shift MUST toggle the alternate symbols page.
- **FR-023**: Starting input (`onStartInput` / `onStartInputView`) MUST reset to the letter layer with Shift off and picker dismissed while preserving the last active language.
- **FR-024**: For `TYPE_NULL` editors, Latin alphanumerics, Space, and common US punctuation MUST be sent as key events (Shift meta for uppercase / shifted punctuation); unmappable characters MAY use `commitText`.
- **FR-025**: Russian number-row 11th pad MUST be inert (`NOOP`).
- **FR-026**: Suggestion strip MUST be suppressed for password / `TYPE_NULL` / non-text / `NO_SUGGESTIONS` / email / URI / filter fields.
- **FR-027**: Emoji panel height and first-frame inset estimates MUST match letter-layer content height for orientation, number row, and suggestion-strip visibility.
- **FR-009**: Keys with alternates MUST support long-press variant popup selection when long-press is enabled.
- **FR-010**: Backspace, Space, and Enter/editor-action keys MUST work according to the focused field’s editor info.
- **FR-011**: Phone portrait and landscape MUST both present a usable bottom soft keyboard; landscape MUST NOT force fullscreen extract IME UI.
- **FR-012**: Users MUST open a full offline emoji panel with category tabs, recents, bottom ABC return, and Unicode insertion (no GIF/sticker tabs; no network emoji packs).
- **FR-013**: Settings UI MUST be in Russian and expose theme selection limited to Light, Dark, and OLED Dark (no system-follow mode in this feature). The settings Activity MUST apply the same selected theme to its own surfaces and system bars.
- **FR-014**: Selected theme MUST persist and apply to keyboard chrome and keys; Dark MUST use neutral charcoal greys (no blue slate); OLED Dark MUST use pure black surfaces.
- **FR-015**: Product MUST NOT implement swipe-typing.
- **FR-016**: Product MUST provide an offline suggestion strip (completions and optional next-word) with user toggles; MUST NOT use Google cloud suggestion/voice services or require network for candidates.
- **FR-017**: Product MUST NOT use Google/Gboard trademarks, logos, or proprietary visual assets.
- **FR-018**: All user-visible settings strings and on-keyboard affordances shipped by this feature MUST be Russian where textual (key letter labels remain script-appropriate for EN/RU layouts; spacebar shows Russian language names).
- **FR-019**: Users MUST be able to toggle an always-visible number row; when on, letter-key digit hints are suppressed.
- **FR-020**: Users MUST be able to configure key borders, sound, haptic, press popup, long-press enablement/delay, auto-capitalization, and double-space period.
- **FR-021**: Spacebar MUST display the active language’s Russian name; globe key MUST be omitted when fewer than two languages are enabled.
- **FR-022**: IME window insets MUST keep the focused editor visible above the keyboard; bottom padding MUST reserve the system IME nav strip so the gap from the screen bottom edge to the spacebar matches contemporary Gboard (~54 dp), and the strip MUST NOT cover the bottom key row.

### Key Entities

- **Input Language**: EN or RU; enabled flag; last-active marker.
- **Keyboard Layer**: Letters, Symbols/Numbers, Emoji panel; start-input resets to Letters.
- **Theme Preference**: Light, Dark, or OLED Dark.
- **Key Definition**: Layout key with action (`COMMIT`, `NOOP`, modifiers, layer keys, …); `NOOP` is the inert RU number-row pad.
- **Key Variant Set**: Optional alternates for a key on long-press.
- **Recent Emoji List**: Ordered recently inserted emoji for the recents category.
- **Suggestion Candidate**: Offline completion or next-word string shown in the strip (suppressed for password/email/`TYPE_NULL`/…).
- **Typing Preferences**: Number row, emoji key visibility, feedback, long-press, suggestions, auto-cap, double-space, key borders.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: A new user can enable freekeyboard and type a mixed EN/RU sentence in under 3 minutes without developer tools.
- **SC-002**: Language switch from EN to RU (or reverse) via globe takes under 1 second from control activation to usable layout.
- **SC-003**: In both portrait and landscape on a phone, 100% of primary letter keys remain tappable without requiring fullscreen extract mode.
- **SC-004**: User can insert at least one emoji from a category browse path and return to letters via ABC in under 30 seconds.
- **SC-005**: After selecting each of the three themes, keyboard appearance visibly matches Light, Dark, and OLED (pure black) on next show, with Russian settings labels throughout.
- **SC-006**: Manual test pass checklist for P1 stories completes with zero blocker defects on Android 12+ phone hardware or emulator.
- **SC-007**: Human-emulation typing of `Hello` + language switch + `да` succeeds with correct scripts in the editor.
- **SC-008**: Double-space period and long-press accent insertion succeed when those preferences are enabled.
- **SC-009**: Opening emoji (with or without number row / landscape) does not change IME content height relative to the letter layer; a new focused field opens on the letter layer, not Symbols/Emoji.

## Assumptions

- Target devices are phones running Android 12 or newer; tablets/foldables are out of scope.
- “Gboard-like” means behavioral and visual parity for covered controls, not pixel-perfect cloning of proprietary assets.
- Voice input, GIF/stickers, clipboard history, glide typing, translate, and floating keyboard remain out of scope.
- Suggestion dictionaries are offline FLOSS/static data bundled with the app; personal learning dictionaries may be specified later.
- Users grant the normal Android IME enablement permission via system settings.
- Emoji coverage uses platform-available emoji glyphs; no proprietary emoji font pack is required for v1.
- Default enabled input languages are both EN and RU.
- Theme choice is explicit user selection (not forced to follow system).
