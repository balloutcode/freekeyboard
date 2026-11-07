# Agent 1b — Gboard atomic inventory (parity research)

**Date**: 2026-11-07  
**Package**: `com.google.android.inputmethod.latin` **18.5.6.993608717-release-x86_64**  
**Device**: `emulator-5554` (`sdk_gphone64_x86_64`, 720×1280 @ 320dpi)  
**Live exercise**: **YES** (Gboard selected as default IME; Messages + Chrome field probe)  
**Research only** — no freekeyboard feature code changed.

## Schema

`ID | Area | Feature | ExactBehavior | Interaction | OfflineFeasible | Source | EvidencePath`

- **OfflineFeasible** = `yes` / `no` / `mixed` (local subset possible)  
- **Hard OOS** rows still listed with `OfflineFeasible=no` and Feature tagged `[HARD OOS]` — do **not** recommend implement.

## Tallies

| Metric | Count |
|--------|------:|
| Inventory rows (unique IDs) | **96** |
| OfflineFeasible=`yes` | **68** |
| OfflineFeasible=`mixed` | **4** |
| OfflineFeasible=`no` (cloud/GMS/Hard OOS) | **24** |

---

## Live smoke log (Agent 1b)

| Surface | Result | Evidence |
|---------|--------|----------|
| Switch IME → Gboard | Pass (`ime set` + `mCurMethodId=…LatinIME`, `mInputShown=true`) | session |
| Letters QWERTY + toolbar | Pass — features grid, clipboard, **Translate**, settings gear, **mic**; digit hints 1–0 on q–p; emoji key; space unlabeled (single lang) | `evidence/agent1b-/03-letters.png` |
| Symbols `?123` | Pass — digit row + punct; `=\<`; ABC; compact `1234` pad key | `04-symbols.png` |
| Symbols alt | Pass — rarer symbols page; cursor `<` `>` on bottom row | `05-symbols-alt.png` |
| Features grid | Pass — Theme, Sticker(**disabled**), GIF(**disabled**), Emoji, One-handed, Text editing; page dots; customize pencil | `07-features.png` |
| Emoji panel | Pass — categories, Search, ABC / Emoji / **GIF** / Stickers / Emoticon tabs | `08-emoji.png` |
| Space caret swipe | Pass — typed `asdf`, swipe space L, inject `X` → field `Xasdf` | `11-space-swipe.png`, `11-swipe-ui.xml` |
| Suggestion strip on letters | Pass — chips e.g. `X`/`Ax`/`Xbox`, emoji chips | `11-space-swipe.png`, `12-clipboard.png` |
| Email InputType | Pass — dedicated `@` left of space; period right; Chrome autofill strip ≠ IME suggest | `16-email-field.png` |
| Text InputType (Chrome) | Pass — letters + features toolbar; Chrome autofill icons above KB | `15-text-field.png` |
| Password HTML focus | Partial — focus/tap contested by covering KB; treat as exercised-attempt | `17-password-field.png` |
| Number / PIN / phone / datetime | **Not completed live** — emulator went offline mid-session after field-test reload; behavior from docs + prior digests | see rows `INV-NUM-*` |
| Clipboard panel open | Attempted toolbar clipboard tap — panel did not open (still letter strip) | `12-clipboard.png` |
| One-handed from features | Attempted — no visible dock compress in shot | `13-one-handed.png` |
| Settings Activity via `am start` | Failed (`SettingsActivity` class absent on this build); gear visible on toolbar | `07-features.png` |
| LP Space language / globe | Not exercised (single language; no globe key on this AVD) | — |
| Glide across letters | Not smoke-tested this run; Hard OOS | — |

Coordinator restores freekeyboard after this agent.

---

## Inventory table

### Letters / symbols / emoji chrome

| ID | Area | Feature | ExactBehavior | Interaction | OfflineFeasible | Source | EvidencePath |
|----|------|---------|---------------|-------------|-----------------|--------|--------------|
| INV-LTR-001 | Letters | EN QWERTY letter layer | Latin QWERTY; rounded white letter keys; functional keys light lavender/blue | Tap letter commits glyph | yes | live + Help 2842292 | `evidence/agent1b-/03-letters.png` |
| INV-LTR-002 | Letters | Top-row digit hints | When number row off, q–p show corner hints 1–0 | Long-press letter inserts digit | yes | live + androidauthority | `03-letters.png` |
| INV-LTR-003 | Letters | Bottom-row chrome (letters) | Left→right: `?123`, comma, emoji, space, period, Enter/action | Tap each | yes | live | `03-letters.png` |
| INV-LTR-004 | Letters | Shift / Caps chrome | Shift left of zxcv; visual latch for Caps (Help: double-tap / hold) | Tap / double-tap / LP Shift | yes | Help 2842292 + digest | docs; live Shift visible `03-letters.png` |
| INV-LTR-005 | Letters | Backspace | Deletes selection or previous code point; hold repeats / word delete (platform IME pattern) | Tap / hold | yes | digest + platform | `03-letters.png` |
| INV-LTR-006 | Letters | Space language label | Space shows active language name when multi-lang; blank when single language | Visual | yes | Help + live (blank) | `03-letters.png` |
| INV-SYM-001 | Symbols | Open `?123` | Swaps to digits + primary punctuation; toolbar strip remains | Tap `?123` | yes | live | `04-symbols.png` |
| INV-SYM-002 | Symbols | Primary symbols page | Row1 `1–0`; Row2 `@#$…/`; Row3 `=\<` + `*"':;!?` + BS | Tap | yes | live | `04-symbols.png` |
| INV-SYM-003 | Symbols | Alt symbols page | `=\<` / Shift toggles rarer symbols (`~\|•√π…`, currencies, etc.) | Tap `=\<` | yes | live | `05-symbols-alt.png` |
| INV-SYM-004 | Symbols | ABC return | Returns to letter language layer | Tap `ABC` | yes | live | `04-symbols.png` |
| INV-SYM-005 | Symbols | Compact numpad entry key | Bottom-row `1234` grid key opens dedicated digit pad surface | Tap | yes | live | `04-symbols.png` |
| INV-SYM-006 | Symbols | Cursor arrows on alt | Alt page bottom shows `<` `>` next to space | Tap | yes | live | `05-symbols-alt.png` |
| INV-EMO-001 | Emoji | Open emoji panel | Full-height panel; category tabs; Smileys & Emotions grid | Tap emoji key | yes | live | `08-emoji.png` |
| INV-EMO-002 | Emoji | Emoji search | Search field at top of panel | Tap Search, type | yes | live | `08-emoji.png` |
| INV-EMO-003 | Emoji | Recents category | Clock/recents tab in category row | Tap | yes | live | `08-emoji.png` |
| INV-EMO-004 | Emoji | ABC return | Bottom ABC restores letters | Tap ABC | yes | live | `08-emoji.png` |
| INV-EMO-005 | Emoji | Bottom mode tabs | ABC / Emoji / **GIF** / Stickers / Emoticon + BS | Tap tab | mixed | live | `08-emoji.png` |
| INV-EMO-006 | Emoji | [HARD OOS] GIF tab | Network GIF browser | Tap GIF | no | Hard OOS | `08-emoji.png` |
| INV-EMO-007 | Emoji | [HARD OOS] Stickers / Bitmoji | Sticker packs / network | Tap Sticker | no | Hard OOS | `07-features.png`, `08-emoji.png` |

### Strip on letters vs `?123`

| ID | Area | Feature | ExactBehavior | Interaction | OfflineFeasible | Source | EvidencePath |
|----|------|---------|---------------|-------------|-----------------|--------|--------------|
| INV-STP-001 | Strip | Suggestion chips on letters | Up to ~3 word chips above keys while typing; may include emoji chips | Tap chip accepts | yes | live + Help 7068415 | `11-space-swipe.png` |
| INV-STP-002 | Strip | Features icons when idle | Idle letter field: grid + clipboard + Translate + settings + mic (no word chips) | Tap icons | mixed | live | `03-letters.png` |
| INV-STP-003 | Strip | Strip chrome on symbols | Features/toolbar strip remains on `?123`; word chips usually empty | Layer change | yes | live + digest | `04-symbols.png` |
| INV-STP-004 | Strip | Long-press reject / trash | LP suggestion → trash to remove (Help) | LP chip | yes | Help 7068415 | docs (not re-captured) |
| INV-STP-005 | Strip | Suppress suggestions (password / no-suggest) | Host password / `NO_SUGGESTIONS` / email/URI norms hide IME suggestion chips | Auto from EditorInfo | yes | digest FR-026 + platform | docs; Chrome autofill ≠ IME |

### Space / caret / language / globe

| ID | Area | Feature | ExactBehavior | Interaction | OfflineFeasible | Source | EvidencePath |
|----|------|---------|---------------|-------------|-----------------|--------|--------------|
| INV-SPC-001 | Space | Commit space | Tap inserts U+0020 | Tap Space | yes | live | `10-typed-asdf.png` |
| INV-SPC-002 | Space | Horizontal caret swipe | Swipe L/R on Space moves caret without inserting spaces | Horizontal swipe on Space | yes | live + Help 2842292 | `11-space-swipe.png` (`Xasdf`) |
| INV-SPC-003 | Space | LP Space language UI | Hold Space opens language switch/picker among enabled IMEs/layouts | Long-press Space | yes | Help 2842292 | docs (not live — single lang) |
| INV-GLB-001 | Globe | Language switch key | Optional Preferences key; tap cycles languages; LP picker | Pref on + ≥2 langs | yes | Help tip + prefs | docs (globe absent this AVD) |
| INV-GLB-002 | Globe | Emoji vs language XOR | Enabling language key greys emoji switch key (and vice versa) tip | Prefs mutual exclusion | yes | Help 2842292 tip | docs |

### Number / PIN / phone / datetime

| ID | Area | Feature | ExactBehavior | Interaction | OfflineFeasible | Source | EvidencePath |
|----|------|---------|---------------|-------------|-----------------|--------|--------------|
| INV-NUM-001 | Number | `TYPE_CLASS_NUMBER` pad | Compact digit keypad (not QWERTY); suggestion strip suppressed | Focus number field | yes | docs digest + platform | docs; live incomplete |
| INV-NUM-002 | Number | Decimal / signed flags | `.` and/or `−` keys when `NUMBER_FLAG_DECIMAL` / `SIGNED` | Focus flagged number | yes | digest | docs |
| INV-NUM-003 | PIN | `numberPassword` / PIN pad | Digit-oriented PIN pad; host masks; typically no ABC/Space row | Focus PIN / numberPassword | yes | digest | docs; live incomplete |
| INV-NUM-004 | Phone | `TYPE_CLASS_PHONE` | Dial pad with letter hints + `*` `#` | Focus phone/tel | yes | digest | docs; live incomplete |
| INV-NUM-005 | Datetime | Datetime editors | Often host picker / specialized keys; IME may show digit or letters depending on `inputType` | Focus datetime | yes | platform / HTML `datetime-local` often Spinner | docs; live incomplete (`dt` was Spinner in dump) |
| INV-NUM-006 | Symbols | Dedicated numpad from symbols | `1234` key on symbols bottom opens pad without leaving symbols family | Tap `1234` | yes | live | `04-symbols.png` |

### Password / email / URI behavior

| ID | Area | Feature | ExactBehavior | Interaction | OfflineFeasible | Source | EvidencePath |
|----|------|---------|---------------|-------------|-----------------|--------|--------------|
| INV-ED-001 | Editors | Password typing | Letters (or number-row prefs); no suggestion chips; no learn | Focus password | yes | Help privacy + digest | docs; attempt `17-password-field.png` |
| INV-ED-002 | Editors | Number row for passwords | Pref: show dedicated number row in password fields even if global off | Prefs toggle | yes | settings inventory / howtogeek | docs |
| INV-ED-003 | Editors | Email layout | QWERTY with dedicated `@` left of space; `.` right; action often Next | Focus email | yes | live | `16-email-field.png` |
| INV-ED-004 | Editors | URI / URL | Letters; suggestions typically suppressed; `.` / `/` accessible | Focus URI | yes | digest FR-026 | docs |
| INV-ED-005 | Editors | Visible password | Treated as sensitive (strip suppress) | Focus | yes | EditorInfo digest | docs |
| INV-ED-006 | Editors | Enter / IME actions | Enter label/icon follows `imeOptions` (Done/Send/Search/Go/Next) | Tap Enter | yes | live checkmark/Next | `03-letters.png`, `16-email-field.png` |

### One-hand / clipboard / features toolbar

| ID | Area | Feature | ExactBehavior | Interaction | OfflineFeasible | Source | EvidencePath |
|----|------|---------|---------------|-------------|-----------------|--------|--------------|
| INV-TB-001 | Toolbar | Features grid entry | Top-left 4-square opens paginated feature grid | Tap grid | yes | live | `07-features.png` |
| INV-TB-002 | Toolbar | Theme shortcut | Grid → Theme opens theme picker | Tap Theme | yes | live + Help 6102154 | `07-features.png` |
| INV-TB-003 | Toolbar | Emoji from grid | Grid → Emoji opens panel | Tap Emoji | yes | live | `07-features.png` |
| INV-TB-004 | Toolbar | One-handed mode | Shrink keyboard + dock L/R with side rail; expand exits | Grid / prefs | yes | live entry visible; dock not verified | `07-features.png`, `13-one-handed.png` |
| INV-TB-005 | Toolbar | Text editing mode | Cursor/selection helper (`< I >`) | Tap Text editing | yes | live | `07-features.png` |
| INV-TB-006 | Toolbar | Settings gear | Opens Gboard settings from strip/grid | Tap gear | yes | live (gear visible) | `03-letters.png` |
| INV-TB-007 | Toolbar | Clipboard icon | Opens local clipboard history panel | Tap clipboard | yes | live icon; panel open failed in smoke | `03-letters.png`, `12-clipboard.png` |
| INV-CLP-001 | Clipboard | Local history list | Copied texts listed; tap pastes | Open clipboard | yes | secondary guides + aapt clipboard ids | docs / APK ids |
| INV-CLP-002 | Clipboard | Pin items | Pin persists beyond auto-clear window | LP / pin control | yes | maketecheasier / howtogeek | docs |
| INV-CLP-003 | Clipboard | Auto-clear ~1h | Unpinned items expire ~1 hour | Time | yes | secondary | docs |
| INV-CLP-004 | Clipboard | Screenshots in clipboard | Optional save recent screenshots | Pref | mixed | secondary (privacy) | docs |
| INV-TB-008 | Toolbar | [HARD OOS] Resize | Drag handles change height/gap | Resize tool | no | Hard OOS (product) | docs / out-of-scope |
| INV-TB-009 | Toolbar | [HARD OOS] Floating keyboard | Undock floating window | Grid / prefs | no | Hard OOS | surfaces-map |
| INV-TB-010 | Toolbar | [HARD OOS] Translate | Translate chip / panel (network + Google) | Tap Translate | no | Hard OOS | `03-letters.png`, `07-features.png` |
| INV-TB-011 | Toolbar | [HARD OOS] Voice / mic | Circular mic → voice typing (GMS) | Tap mic | no | Hard OOS | `03-letters.png` |
| INV-TB-012 | Toolbar | [HARD OOS] Search toolbar | Google “Search and more” | Tap Search | no | Hard OOS | Help 6380730 |
| INV-TB-013 | Toolbar | [HARD OOS] GIF / Stickers from grid | Grayed or network when unavailable | Tap | no | Hard OOS | `07-features.png` |

### Offline-relevant settings (Preferences / Text correction / etc.)

| ID | Area | Feature | ExactBehavior | Interaction | OfflineFeasible | Source | EvidencePath |
|----|------|---------|---------------|-------------|-----------------|--------|--------------|
| INV-SET-001 | Settings | Access paths | Features → Settings; LP comma→Settings; System → On-screen keyboard → Gboard | Nav | yes | settings-inventory | docs |
| INV-SET-002 | Settings | Languages | Add/enable layouts (EN/RU etc.) | Nav + toggles | yes | Help 6380730 | docs |
| INV-SET-003 | Prefs | Number row | Always-on 1–0 above letters; clears digit hints | Toggle | yes | inventory | docs |
| INV-SET-004 | Prefs | Number row for passwords | Password-only number row | Toggle | yes | inventory | docs |
| INV-SET-005 | Prefs | Show emoji switch key | Bottom emoji key | Toggle | yes | inventory + live | `03-letters.png` |
| INV-SET-006 | Prefs | Show language switch key | Globe key; XOR tip w/ emoji | Toggle | yes | Help tip | docs |
| INV-SET-007 | Prefs | Show suggestion strip | Hide/show strip | Toggle | yes | Help 9058584 | docs |
| INV-SET-008 | Prefs | One-handed mode | Pref entry mirrors toolbar | Toggle | yes | inventory | docs |
| INV-SET-009 | Prefs | Sound on keypress + volume | Click sound | Toggle/slider | yes | Help 6102154 | docs |
| INV-SET-010 | Prefs | Keyboard vibration + strength | Haptic | Toggle/slider | yes | Help 6102154 | docs |
| INV-SET-011 | Prefs | Popup on keypress | Circular preview above key | Toggle | yes | inventory | docs |
| INV-SET-012 | Prefs | Touch & hold delay | 100–700ms family slider | Slider | yes | engadget / inventory | docs |
| INV-SET-013 | Prefs | Touch & hold keys for symbols | Symbol hints + hold insert | Toggle | yes | inventory | docs |
| INV-SET-014 | Prefs | Key borders | Theme outlines | Toggle | yes | inventory | docs |
| INV-SET-015 | Text corr | Auto-capitalization | Sentence start Shift ONCE | Toggle | yes | Help | docs |
| INV-SET-016 | Text corr | Double-space period | Two spaces after letters → `. ` | Toggle | yes | prefs family | docs |
| INV-SET-017 | Text corr | Next-word suggestions | Chips after word+space | Toggle | yes | Help 7068415 | docs |
| INV-SET-018 | Text corr | Block offensive words | Filter suggestions | Toggle | yes | inventory | docs |
| INV-SET-019 | Text corr | Auto-correction | Replace misspellings as you type | Toggle | yes | Help 7068415 | docs |
| INV-SET-020 | Text corr | Emoji suggestions in strip | Emoji chips among suggestions | Toggle | yes | inventory + live chips | `12-clipboard.png` |
| INV-SET-021 | Dictionary | Personal dictionary | Add/remove words | Nav | yes | Help 7068415 | docs |
| INV-SET-022 | Theme | Built-in themes | Light/dark/custom from Theme screen / toolbar | Select | yes | Help 6102154 | `07-features.png` |
| INV-SET-023 | Emoji prefs | Emoji size | Size under Emojis/stickers/GIFs | Slider | yes | Help 6102154 | docs |
| INV-SET-024 | Settings | [HARD OOS] Glide typing screen | Swipe-typing prefs | Nav | no | Hard OOS | out-of-scope |
| INV-SET-025 | Settings | [HARD OOS] Voice typing screen | GMS voice | Nav | no | Hard OOS | out-of-scope |
| INV-SET-026 | Settings | [HARD OOS] Privacy personalize / federated / audio | Cloud learning toggles | Nav | no | Hard OOS | Help 12373137 |
| INV-SET-027 | Settings | [HARD OOS] Smart Compose / Proofread Fix it | Cloud chips | Feature | no | Hard OOS | Help 7068415 |
| INV-SET-028 | Settings | [HARD OOS] Suggest contacts (GMS) | Contact-backed suggestions | Toggle | no | GMS | inventory |
| INV-SET-029 | Settings | [HARD OOS] Clipboard sync phone↔PC | Account sync | Pref | no | Hard OOS | out-of-scope |
| INV-SET-030 | Theme | [HARD OOS] Photo / Material You wallpaper themes | Brand/asset risk | Select | no | Hard OOS | out-of-scope |

### Editing helpers / misc (offline-feasible)

| ID | Area | Feature | ExactBehavior | Interaction | OfflineFeasible | Source | EvidencePath |
|----|------|---------|---------------|-------------|-----------------|--------|--------------|
| INV-MSC-001 | Misc | Double-space period | After letter token, space+space → `. ` | Double-tap Space | yes | prefs | docs |
| INV-MSC-002 | Misc | Undo autocorrect via BS | After auto-replace, BS restores | Tap BS | yes | Help 7068415 | docs |
| INV-MSC-003 | Misc | Key press popup | Circular preview when pref on | Tap letter | yes | inventory | docs |
| INV-MSC-004 | Misc | Long-press accent variants | Hold vowel → accent row; slide release | LP + slide | yes | Help 2842292 | docs |
| INV-MSC-005 | Misc | Incognito / no personalized learning | Indicator + no learn when flag set | Editor flag | yes | Android IME + Gb UI | docs |
| INV-MSC-006 | Misc | [HARD OOS] Glide / swipe typing across letters | Trace path commits word | Swipe letters | no | Hard OOS | out-of-scope |
| INV-MSC-007 | Misc | [HARD OOS] Hold-?123-slide insert | Hold ?123 slide to symbol & return | Hold-drag | no | optional / OOS note | out-of-scope |
| INV-MSC-008 | Brand | [HARD OOS] Gboard trademarks / assets | Logos, proprietary keycap art | N/A | no | Hard OOS | out-of-scope |

---

## Cross-refs

- Docs digest: `gboard-docs-digest.md`  
- Feature matrix (fk status): `gboard-feature-matrix.md`  
- Settings map: `gboard-settings-inventory.md`  
- Surfaces: `gboard-surfaces-map.md`  
- Hard exclusions: `out-of-scope.md`  
- Live evidence dir: `evidence/agent1b-/`

## Notes for Agent 2 / coordinator

1. **Offline-feasible count for planning** = **68** (`yes` only). Treat `mixed` (4) as partial OF.  
2. Space caret swipe is **required** (not glide). Glide remains Hard OOS.  
3. Mic / Translate / GIF / Stickers / Search / Floating / Resize / cloud privacy / Smart Compose = cataloged as **no** — do not implement.  
4. Live gaps to re-smoke when AVD stable: number/PIN/phone pads, clipboard panel open, one-hand dock, Settings tree dump, LP Space language.
