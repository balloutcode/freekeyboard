# Parity audit — 2026-11-07 (clarified)

## Product clarification (important)

User meant **glide / gesture typing** (drawing words across keys) when saying «вырезать свайпы» — **not** spacebar cursor swipe.

| Gesture | Status |
|---------|--------|
| **Spacebar swipe L/R → move caret** | **In-scope Done** (`A07-007` / `A21-005`) |
| **Glide / swipe-typing** | **Hard OOS** (constitution + product) |
| Hold-?123-slide / drag-to-trash | Optional / not required |
| Keyboard resize | Hard OOS (product removed) |

## In-scope leftovers (low priority)

| ID | Why |
|----|-----|
| A16-005 | TYPE_NULL Cyrillic = `commitText` fallback |
| A20-001/002/004 | TalkBack / Switch Access P2 |
| A21-011 | Composing span optional P2 |

Hard OOS (glide/voice/translate/GIF/floating/cloud/search/mic/resize) intentionally absent.
