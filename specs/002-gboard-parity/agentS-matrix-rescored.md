# Agent S — Matrix rescore deltas

**Date**: 2026-11-07  
**Baseline statuses**: `gboard-feature-matrix.md` (Agent 2 TZ, post-pass) + adversarial `agent3-matrix-rescored.md`  
**Rule**: Code/tests win. Hard OOS rows listed only when Agent 3 wrongly treated them as gaps.

Statuses: Done | Partial | Missing | Incorrect | Hard OOS (not a gap)

---

## Summary vs Agent 3

| Priority | Agent3 Done% | Agent S Done% | Delta |
|----------|-------------:|--------------:|------:|
| P0 | 90.7% | **100%** | +9.3 pp (strip/swipe/learn/chrome closed) |
| P1 | 68.3% | **97.4%** | +29.1 pp (prefs/toolbar/emoji/dict + resize excluded from %) |
| P2 | 25.0% | **58.3%** | +33.3 pp (emoji search/size, fontScale) |
| All | 77.3% | **95.2%** | +17.9 pp |

In-scope denominator: **126** (`yes`/`yes*`). Agent 3 used 128 by including resize `A11-003`/`A14-010` — those are now **Hard OOS**.

---

## Rows that changed vs Agent 3 (material)

| ID | Pri | Matrix (current) | Agent3 | **Agent S** | Evidence |
|----|-----|------------------|--------|-------------|----------|
| A02-004 | P0 | Done | Partial | **Done** | Bottom row layouts include emoji/globe/space; emoji gated by `showEmojiKey` (default true). AVD pref-off ≠ Missing |
| A07-007 | P0 | Done | Partial | **Done** | `SpaceCursorSwipe` + `KeyGrid` + `onSpaceCursorSwipe` → `moveCursorBy`; `SpaceCursorSwipeTest` |
| A10-006 | P0 | Done | Incorrect | **Done** | `KeyboardRoot`: `!suppressSuggestions` before mounting `SuggestionBar` |
| A10-007 | P1 | Done | Partial | **Done** | LP reject via `RejectedSuggestionsStore`; drag-to-trash optional/OOS |
| A11-001 | P1 | Done | Partial | **Done** | `FeaturesMenu` + apps affordance in `SuggestionBar` |
| A11-002 | P1 | Done | Partial | **Done** | One-hand `weight(2f)` body + 36dp rail + flip/expand (~2/3 dock) |
| A11-003 | P1 | Missing / no | Missing | **Hard OOS** | Resize removed; not an in-scope gap |
| A12-001 | P0 | Done | Partial | **Done** | `EmojiPanel` + `KeyAction.EMOJI`; FeaturesMenu emoji entry |
| A12-006 | P2 | Done | Missing | **Done** | `EmojiSearch` + search field in `EmojiPanel`; `EmojiSearchTest` |
| A12-007 | P2 | Done | Missing | **Done** | `emojiFontScale` pref + slider + panel `fontSize` |
| A12-008 | P1 | Done | Partial | **Done** | `showEmojiKey` filter; `FeaturesMenu.onEmoji` when key hidden |
| A14-006 | P1 | Done | Partial | **Done** | `soundVolume` slider → `KeyFeedback.play` |
| A14-007 | P1 | Done | Partial | **Done** | `hapticStrength` slider → amplitude/duration |
| A14-010 | P1 | Missing / no | Missing | **Hard OOS** | Same as A11-003 |
| A15-003 | P1 | Done | Partial | **Done** | Settings: number row, feedback, LP, volume, haptic, emoji size, autocorrect, clipboard, one-hand |
| A15-005 | P2 | Done | Missing | **Done** | Personal dictionary UI + DataStore + `SuggestionEngine.setPersonalWords` |
| A16-001 | P0 | Done | Incorrect | **Done** | Strip hidden when suppress (shared with A10-006) |
| A16-002 | P0 | Done | Partial | **Done** | Same suppress gate |
| A16-003 | P0 | Done | Partial | **Done** | Same suppress gate |
| A19-002 | P0 | Done | Partial | **Done** | `recordTypedToken` on space/autocorrect; `LearningPolicy` password gate + tests |
| A20-003 | P2 | Done | Missing | **Done** | `KeyGrid` `fontScale.coerceIn(1f, 1.35f)` |
| A21-005 | P0 | Done | Partial | **Done** | Alias of A07-007 |

---

## In-scope rows still not Done (Agent S agrees with matrix)

| ID | Pri | Old (matrix) | Agent S | Evidence |
|----|-----|--------------|---------|----------|
| A16-005 | P1 | Partial | **Partial** | TYPE_NULL non-ASCII → `commitText` fallback in `InputConnectionFacade.sendKeyChar` |
| A20-001 | P2 | Partial | **Partial** | `contentDescription = label` on keys; TalkBack polish incomplete |
| A20-002 | P2 | Partial | **Partial** | Chips clickable; limited chip announcements |
| A20-004 | P2 | Missing | **Missing** | No Switch Access specialization |
| A21-011 | P2 | Missing | **Missing** | No composing span (optional / not required) |
| A21-012 | P2 | Partial | **Partial** | Word delete without batch-edit wrapper |

---

## Full P0 in-scope rescore (Agent S)

All **75** P0 in-scope rows: **Done**.

Notable evidence samples:

| ID | Agent S | Evidence |
|----|---------|----------|
| A01-001..003 | Done | Manifest / settings enable+select IME |
| A02-001..005 | Done | `EnQwerty` / `RuJcuiken` / metrics / bottom row |
| A03-001..006 | Done | Symbols layers + Shift LP |
| A04-001..005 | Done | Number row / hints / RU pad NOOP |
| A05-001..006,008,009 | Done | Shift / Caps / auto-cap / NOOP |
| A06-001,002,006 | Done | Variants + `RuVariantsTest` |
| A07-001..004,006,007 | Done | Space/globe/label + **SpaceCursorSwipe** |
| A08-001..005 | Done | Delete / word delete / code points |
| A09-001..004 | Done | Enter actions / TYPE_NULL Enter |
| A10-001,002,006,008,010 | Done | Suggest + suppress gate + RU dict |
| A12-001,004 | Done | Emoji panel + shared letter height |
| A14-009 | Done | `IME_NAV_STRIP_DP=54` |
| A15-008 | Done | About without Gboard trademark |
| A16-001..004,007,008 | Done | Password/email/URI/TYPE_NULL Latin / flags |
| A17-001..003 | Done | Portrait/landscape / no extract |
| A18-001..004 | Done | Lang persist / enabled set / onStartInput |
| A19-002,003 | Done | LearningPolicy wired; offline suggest |
| A21-001,002,005,006,009,010 | Done | Double-space / caret swipe / Shift OFF / emoji commit |

---

## Full P1 in-scope rescore (Agent S)

| ID | Matrix | Agent S | Notes |
|----|--------|---------|-------|
| A01-004,005 | Done | Done | Subtypes + settingsActivity |
| A04-006 | Done | Done | `numberRowForPasswords` + test |
| A05-007 | Done | Done | auto-cap pref |
| A06-003..005 | Done | Done | Fractions / delay / disable LP |
| A07-005 | Done | Done | Globe LP picker |
| A10-003..005,007 | Done | Done | Next-word / filter / strip toggle / LP reject |
| A11-001,002,004 | Done | Done | Features / one-hand / theme cycle |
| A12-002,003,008 | Done | Done | Categories / recents / hide emoji key |
| A13-001,002 | Done | Done | Clipboard panel + pin |
| A14-001..008 | Done | Done | Themes / borders / sound / haptic / popup |
| A15-001..004 | Done | Done | RU settings / theme sync / prefs / clipboard |
| A16-005 | Partial | **Partial** | Only remaining P1 gap |
| A16-006 | Done | Done | NumberPad |
| A17-004 | Done | Done | Landscape metrics |
| A18-005 | Done | Done | System locale unchanged (N/A) |
| A19-001 | Done | Done | Incognito indicator + flag |
| A21-003,008 | Done | Done | Double-space pref / NOOP silent |

**P1 Done% = 38/39 = 97.4%**

---

## Hard OOS / out-of-scope rows (not gaps)

| ID | Agent S | Notes |
|----|---------|-------|
| A03-007 | Missing / no | Hold-?123-slide optional |
| A07-008 | Missing / no* | Emoji XOR language — product keeps both |
| A10-009 | Missing / no* | Autocorrect underline product-gated (offline autocorrect exists separately) |
| A11-003 / A14-010 | Missing / no | **Resize Hard OOS** |
| A11-005 / A11-006 | Done (absent) / no | No Search / mic |
| A12-005 | Done (absent) / no | No GIF |
| A13-004 | Missing / no* | Screenshots OOS |
| A15-006 | Missing / no | Privacy federated UI OOS |
| A15-007 | Done (absent) / no | No Glide/Voice/Translate screens |
| A17-005 | Missing / no | Split keyboard OOS |
| A19-004 | Done (absent) / no | No federated learning UI |
| *(no ID)* Glide typing | Absent | Must never be listed as freekeyboard Missing |

---

## Agreement with current matrix status cells

Almost all in-scope status cells already match Agent S. Material matrix doc fix: **tallies / backlog text** that still framed resize as remaining P1 work — corrected in `gboard-feature-matrix.md` tallies section only.
