# Agent 3b — Gboard parity diff (implementation pass)

**Date**: 2026-11-07
**Inputs**: `agent1b-gboard-inventory.md`, `agent2b-freekeyboard-inventory.md`, `agent2-gap-backlog.md`,
`out-of-scope.md`, `gboard-feature-matrix.md`, `agentS-screen-report.md`, `evidence/agentT-emulator-smoke.md`.

Baseline going in (Agent S, 2026-11-07): **PASS_WITH_GAPS**, P0 in-scope Done **100%** (75/75),
P1 in-scope Done **97.4%** (38/39, only `A16-005` Partial). No P0/P1 Missing. Remaining gaps are
all P2 or explicitly optional/OOS. This diff covers every item on Agent 2's "known leftovers" list
plus the six must-verify rows; it does **not** re-litigate the ~120 rows Agent S already scored Done.

## Must-verify (regression guard — no code change expected here)

| Feature | Gboard | fk | Gap | Pri | Decision | Notes |
|---|---|---|---|---|---|---|
| Space horizontal swipe → caret | Yes | Done (`SpaceCursorSwipe`) | None | P0 | Verified | Untouched; re-built + reinstalled; code path unchanged |
| Strip chrome on SYMBOLS (`?123`) | Yes | Done (`KeyboardRoot`) | None | P0 | Verified | Untouched |
| NumberPad variants (PIN/decimal/signed/phone) + `EditorInfoInterpreter` | Yes | Done | None | P0/P1 | Verified | Untouched; `NumberPadLayoutTest`/`EditorInfoInterpreterTest` still green |
| One-hand `0.68f` dock + rail | Yes | Done (`KeyboardRoot`) | None | P1 | Verified | Untouched |
| In-IME `LanguagePicker` (non-Popup) | Yes | Done | None | P0 | Verified | Untouched |
| Strip suppressed on password/email/URI | Yes | Done (`suppressSuggestions`) | None | P0 | Verified | Untouched; confirmed live on RU letters strip still shows chips (screenshot) |

## Known leftovers (Agent 2 → Agent 3b decisions)

| Feature | Gboard | fk (before) | Gap | Pri | Decision | Notes |
|---|---|---|---|---|---|---|
| **A16-005** TYPE_NULL Cyrillic (and other no-keycode chars) | Raw `KeyEvent`s only; many TYPE_NULL hosts ignore `commitText` | Partial — fell back to `InputConnection.commitText` for Cyrillic/emoji | Real gap: TYPE_NULL hosts that truly ignore `commitText` would silently drop Cyrillic | **P1** | **Implement** | `InputConnectionFacade.sendCharacters` now sends one `ACTION_MULTIPLE`/`KEYCODE_UNKNOWN` `KeyEvent(time, text, KeyCharacterMap.VIRTUAL_KEYBOARD, 0)` instead of `commitText` — the same raw-key mechanism Android's own IME fallback path uses. `RawKeyCodes.actionFor()` sealed `Stroke`/`Characters` result is unit-tested |
| **A21-012** Word-delete batch edit | Multi-step IC edits appear atomic to host | Partial — `deleteWordBackward` did multiple IC calls with no batch wrapper | Minor: host `TextWatcher`s could see 2+ intermediate change events | **P2 (cheap)** | **Implement** | Wrapped `deleteWordBackward` body in `ic.beginBatchEdit()` / `finally { ic.endBatchEdit() }`. Behavior-preserving; covered by existing `WordDeleteTest` + emulator smoke (hold-backspace word delete still works) |
| **A20-001** Key `contentDescription` (TalkBack) | Keys expose real names (e.g. "Delete", "Shift") | Partial — icon keys announced their raw glyph (e.g. "⇧", "⌫", "🌐") as `contentDescription` | TalkBack explore-by-touch read meaningless symbols for special keys | **P2 (cheap)** | **Implement** | New `KeyAccessibility.describe()` maps each `KeyAction` to a Russian description (Shift/Caps state, "Удалить", Enter-style-aware label, "Эмодзи", "Смена языка", space includes active language). Letter/digit/punct keys keep their visible glyph (already meaningful read aloud). Unit-tested (`KeyAccessibilityTest`, 6 cases) |
| **A20-002** Suggestion chip announcements | Chips are focusable/activatable by a11y services | Partial — chips only had a raw `pointerInput` tap/long-press gesture, no semantics node | TalkBack/Switch Access could not reliably focus or activate a chip (no accessibility click action existed) | **P2 (cheap)** | **Implement** | `SuggestionBar` chips now carry `Modifier.semantics { contentDescription; onClick{} ; customActions = [Отклонить] }` alongside (not instead of) the existing tap/long-press gesture |
| **A20-004** Switch Access | Basic switch navigation reaches every control | Missing — raw touch-only gestures are invisible to switch/accessibility-click dispatch | Switch Access could not operate keys or suggestion chips at all | **P2 (cheap)** | **Implement (basic)** | Same semantics additions as A20-001/002 give every active key and chip a standard accessibility `onClick` action, which Switch Access (and TalkBack double-tap) invoke instead of a real touch gesture. NOOP key (RU number-pad spacer) explicitly hidden via `hideFromAccessibility()` so it isn't a dead stop in switch scanning. **Not** full custom Switch-Access scanning order/grouping — that remains a larger, unscoped effort |
| **A10-007** Suggestion reject chrome | LP → drag to trash icon | Done — LP already rejects (no drag) | Drag-to-trash chrome explicitly called out as optional/OOS in `out-of-scope.md` | P1 (Done) / drag chrome P2-OOS | **Skip-OOS** (drag chrome only) | No drag/swipe chrome added (matches product decision). Accessibility reject path added as a side effect of A20-002 (custom action), which is a strict improvement, not scope creep |
| **A10-009** Autocorrect "thin" | Auto-replace + underline, cloud Fix-it optional | Done functionally — `autocorrectEnabled` pref + `SuggestionEngine.autocorrectCandidate()` + BS-undo (`lastAutocorrectOriginal`) all wired on the space-commit path | Only the underline/visual affordance is missing; would require switching that one word to `setComposingText` (a bigger, riskier behavioral change tied to A21-011) | P2 | **Defer** | Functional autocorrect (replace + undo) already shipped; cosmetic underline explicitly tied to the optional composing-span work below — not worth the regression risk for a P2 polish item |
| **A15-006** Privacy / delete-learned-data screen | Delete learned words; personalize toggle | Missing | freekeyboard has **no personalization/learning store** (`NoOpLearningStore`) — a "delete learned data" screen would have nothing real to delete; personalize itself is explicitly OOS | P2, in-scope=`no` in matrix | **Defer** | Building a screen with no backing data would be cosmetic filler, not a real fix. Revisit only if/when an actual on-device learning store is built |
| **A21-011** Composing span (`setComposingText`) | Underlined composing text while typing | Missing — freekeyboard commits directly | Matrix explicitly marks this "Optional; not required for P0" | P2 | **Defer** | Switching the whole commit pipeline from direct `commitText` to composing spans is a cross-cutting behavior change (affects autocorrect-undo, TYPE_NULL raw-key path, suggestion strip timing) with real regression risk for a feature flagged optional. Out of scope for this pass |
| **A03-007** Hold-`?123`-slide insert-and-return | Hold ?123, drag to a symbol, release inserts & returns | Missing | `out-of-scope.md`: "Optional; not required for parity" | P2 | **Skip-OOS** | Explicit product decision; no code change |

## Hard OOS confirmed still absent (no change, verified)

Glide typing, resize/height-scale, voice/mic, translate, GIF/stickers, floating keyboard, cloud sync,
Google Search toolbar, Smart Compose, Gboard trademarks — all **confirmed absent** in this pass
(no code added for any of these; `FeaturesMenu`/`EmojiPanel`/`KeyboardRoot` reviewed, no new entry points).

## Tally

| Decision | Count | Items |
|---|---:|---|
| **Implement** | 4 | A16-005, A21-012, A20-001/002/004 (one combined a11y change-set) |
| **Skip-OOS** | 2 | A10-007 drag-to-trash chrome, A03-007 hold-?123-slide |
| **Defer** | 3 | A10-009 underline polish, A15-006 privacy screen, A21-011 composing span |
| **Verified (no change)** | 6 | All must-verify rows |

Net: **4 implemented**, **2 skip-OOS**, **3 deferred**, **6 regression-verified**, **0 P0/P1 gaps remaining**.
