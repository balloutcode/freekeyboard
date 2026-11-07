# 003 — Inline autofill for password managers

**Created**: 2026-11-07  
**Status**: Implemented  
**As-built sync**: 2026-11-08

## Summary

freekeyboard supports Android 11+ **inline autofill suggestions** so third-party password managers (Proton Pass, Bitwarden, KeePassDX, Google Password Manager, etc.) can show credential chips in the IME suggestion strip. Filling is performed by the Autofill / Credential framework; freekeyboard does not store or transmit credentials.

## Functional requirements

| ID | Requirement |
|----|-------------|
| FR-001 | `method.xml` declares `android:supportsInlineSuggestions="true"`. |
| FR-002 | IME implements `onCreateInlineSuggestionsRequest` returning up to 3 themed `InlinePresentationSpec`s. |
| FR-003 | IME implements `onInlineSuggestionsResponse`, inflates chips, and shows them in the strip via Compose `AndroidView`. |
| FR-004 | Autofill chips appear even when word suggestions are suppressed (password / email / URI). |
| FR-005 | Features toolbar (⊞) remains available beside autofill chips **and** on suppressed fields without chips (004). |
| FR-006 | Inline state clears on `onStartInput` / `onFinishInput` / empty response (single reset path — not duplicated from `onStartInputView`). |
| FR-007 | No logging of chip titles, subtitles, or credential content. |

## Privacy

- No network permission; no freekeyboard AutofillService.
- Chip content is owned by the system provider; freekeyboard only hosts inflated `View`s.
- Word learning / offline suggestions remain suppressed on password fields.

## Out of scope

- Implementing an Autofill service or storing passwords.
- Settings toggle for inline autofill (always on when the platform offers chips).
- Passkey UX beyond what the provider puts in inline presentations.
- Guaranteeing chips on stock AVDs without saved credentials (provider datasets required).

## Verification

1. Unit: `InlineAutofillSupport` size / count constants.
2. Device: `dumpsys input_method` → `mInlineSuggestionsEnabled=true` for freekeyboard.
3. Manual: Autofill service with saved login → focus field → chips → tap fills.
4. Emulator smoke (2026-11-07): flag PASS; chips **BLOCKED** when `Max visible datasets: 0` — see `../002-gboard-parity/evidence/autofill-emulator-smoke.md`.
