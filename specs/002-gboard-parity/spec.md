# Feature Specification: 002 Gboard parity (in-scope)

**Feature Branch**: `002-gboard-parity`  
**Created**: 2026-11-07  
**Status**: Implemented — Agent 4 emulator **PASS**; privacy/hardening follow-ups in [004](../004-privacy-hardening/)  
**Input**: `gboard-feature-matrix.md` + Agent 1–4 pipeline  
**As-built sync**: 2026-11-08

## User Scenarios & Testing

### User Story 1 — Spacebar cursor swipe (Priority: P0)

As a typist, I swipe left/right on the spacebar to move the caret without leaving the keyboard.

**Acceptance**: Mid-word caret moves with horizontal space swipe; tap still inserts space; long-press still opens language picker. Truncated buffers / password fields use DPAD (no absolute jump).

### User Story 2 — RU variants & suggestions (Priority: P0)

As a Russian typist, long-press letter keys expose useful variants (ё/ъ/…) and the suggestion strip completes common prefixes such as `прив` → `привет`.

### User Story 3 — Privacy / no-learn (Priority: P0)

Password and `IME_FLAG_NO_PERSONALIZED_LEARNING` fields never write learning data; incognito indicator is visible when the flag is set. Autocorrect / double-space do not mutate password fields. Features toolbar remains available when word chips are suppressed.

### User Story 4 — Offline comfort panels (Priority: P1)

Number/phone pads, password number-row preference, features menu (theme/emoji/one-hand/clipboard/settings), local clipboard history (opt-in; pin + 1h unpinned / 7d pinned TTL; clear on disable), suggestion reject via long-press (persist only outside sensitive fields).

## Requirements

- FOSS/GPL; no Google brand/assets/GMS; offline-first.
- Do not implement glide typing, voice, translate, GIF/network stickers, floating KB, cloud personalization.
- Preserve 001 parity behaviors (Caps, dark charcoal, insets, TYPE_NULL RawKeyCodes, RU NOOP pad, suggestion candidate suppress + toolbar chrome, onStartInput→LETTERS/NUMBER, symbols Shift long-press).
- Clipboard / backup / release minify obligations owned jointly with [004](../004-privacy-hardening/).
- Inline autofill chips owned by [003](../003-inline-autofill/).

## Success Criteria

- In-scope P0 Done; P1 Done except deferred resize / emoji polish (see `tasks.md`).
- Agent 4 live QA **PASS** (`evidence/agent4-qa-report.md`); one-hand Settings sync **FIXED**.
- `:app:assembleDebug`, `:app:assembleRelease`, `:app:testDebugUnitTest` green.
