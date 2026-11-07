# Agent 2 implementation log — cycle 2 (close top gaps)

**Date**: 2026-11-07  
**Goal**: Close Agent 3 top gaps + clone remaining in-scope Gboard functionality (hard OOS excluded).

## Closed this cycle

| ID | Change |
|----|--------|
| A10-006 / A16-001 | `KeyboardRoot` does **not** compose SuggestionBar when `suppressSuggestions` |
| A11-003 / A14-010 | Top drag resize handle + `keyboardHeightScale` persist + settings reset |
| A11-002 | One-hand ~2/3 width dock (`weight(2f)` body + `weight(1f)` spacer) |
| A07-007 / A21-005 | `moveCursorBy` uses Unicode code points |
| A10-007 | Drag-down-to-trash on suggestion chips + LP reject |
| A19-002 | `LearningPolicy.recordTypedToken` on space commit |
| A14-006 / A14-007 | Sound volume + haptic strength sliders |
| A12-006 / A12-007 | Offline emoji search (EN/RU aliases) + emoji size slider |
| A15-003 / A15-005 | Settings parity + personal dictionary |
| A20-003 | Key heights honor system `fontScale` (capped 1.35) |
| A21-004 | Autocorrect on space + BS undo |
| A13 polish | Clipboard pinned / recent sections |

## Still out of scope / deferred

- Glide, voice, translate, GIF, floating, cloud (hard OOS)
- A03-007 hold-?123-slide (optional P2)
- A21-011 composing span (optional P2)
- A10-009 autocorrect underline chrome (product / OOS)
- A15-006 privacy delete-learned (no learn DB)
- A20-001/002/004 TalkBack / Switch Access polish (Partial / unproven)

## Verification

- `:app:compileDebugKotlin` + `:app:testDebugUnitTest` — green after cycle
- New tests: `EmojiSearchTest`, autocorrect/personal-dict cases in `SuggestionEngineTest`
