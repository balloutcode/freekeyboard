# Feature Specification: 002 Gboard parity (in-scope)

**Feature Branch**: `002-gboard-parity`  
**Created**: 2026-11-07  
**Status**: Implemented (Agent 2 slice)  
**Input**: `gboard-feature-matrix.md` + Agent 1 research

## User Scenarios & Testing

### User Story 1 — Spacebar cursor swipe (Priority: P0)

As a typist, I swipe left/right on the spacebar to move the caret without leaving the keyboard.

**Acceptance**: Mid-word caret moves with horizontal space swipe; tap still inserts space; long-press still opens language picker.

### User Story 2 — RU variants & suggestions (Priority: P0)

As a Russian typist, long-press letter keys expose useful variants (ё/ъ/…) and the suggestion strip completes common prefixes such as `прив` → `привет`.

### User Story 3 — Privacy / no-learn (Priority: P0)

Password and `IME_FLAG_NO_PERSONALIZED_LEARNING` fields never write learning data; incognito indicator is visible when the flag is set.

### User Story 4 — Offline comfort panels (Priority: P1)

Number/phone pads, password number-row preference, features menu (theme/emoji/one-hand/clipboard/settings), local clipboard history with pin + 1h expiry, suggestion reject via long-press.

## Requirements

- FOSS/GPL; no Google brand/assets/GMS; offline-first.
- Do not implement glide typing, voice, translate, GIF/network stickers, floating KB, cloud personalization.
- Preserve 001 parity behaviors (Caps, dark charcoal, insets, TYPE_NULL RawKeyCodes, RU NOOP pad, suggestion suppress, onStartInput→LETTERS/NUMBER, symbols Shift long-press).

## Success Criteria

- All in-scope P0 Missing/Partial → Done or Blocked with reason.
- Maximize P1; leftovers listed in `agent2-gap-backlog.md`.
- `:app:assembleDebug` and `:app:testDebugUnitTest` green.
