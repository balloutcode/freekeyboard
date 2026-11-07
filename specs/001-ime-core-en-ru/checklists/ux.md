# UX Requirements Quality Checklist: IME Core EN/RU

**Purpose**: Validate that UX/behavior requirements are complete, clear, and consistent before implementation  
**Created**: 2026-11-06  
**Feature**: [spec.md](../spec.md)  
**Focus**: Landscape, emoji, themes, language switch, Gboard-like interaction without swipe

**Note**: Check items only after human review of requirements quality (not implementation).

## Language switching

- [x] Is globe-key toggle behavior defined when two (or more) languages are enabled, and hidden when fewer than two?
- [x] Is Space long-press language picker specified as an explicit EN/RU chooser?
- [x] Is swipe-to-switch and swipe-typing explicitly forbidden?
- [x] Is last-active language persistence specified across sessions?
- [x] Is behavior defined when only one language remains enabled?

## Landscape and chrome

- [x] Is bottom-docked soft keyboard required in landscape?
- [x] Is fullscreen extract mode explicitly disallowed for phone landscape?
- [x] Is rotation while typing covered (IME stays usable; language retained; layer may reset if input view restarts)?
- [x] Are portrait and landscape both required to keep primary keys tappable?

## Emoji panel

- [x] Are category tabs and recents required?
- [x] Is offline-only / no network emoji pack download stated?
- [x] Are GIF/sticker tabs explicitly out of scope?
- [x] Is return-to-letters (ABC) restoring previous language specified?
- [x] Is Unicode insertion fallback specified when rich content is unsupported?

## Themes and settings

- [x] Are exactly three themes (Light, Dark, OLED Dark) specified without system-follow?
- [x] Is OLED pure-black surface requirement unambiguous?
- [x] Is Dark specified as neutral charcoal (no blue slate)?
- [x] Must settings Activity follow the same theme preference as the keyboard?
- [x] Is Russian-only settings UI required for all labels in this feature?
- [x] Is theme persistence across keyboard hide/show specified?

## Shift / Caps Lock

- [x] Is single-tap Shift defined as one-shot capital (`ONCE`)?
- [x] Is Caps Lock defined as double-tap and/or long-press Shift (Gboard parity)?
- [x] Is unlock defined as a single tap while `CAPS` is latched?
- [x] Is symbols-layer Shift long-press defined to toggle the alternate page (not a no-op)?

## Number row / height / start-input

- [x] Is the RU 11th number-row pad specified as inert (no commit / no Shift consume)?
- [x] Is emoji / first-frame height parity with letter-layer metrics (orientation + number row + suggestion strip) specified?
- [x] Is start-input reset to letters (preserve language) specified?

## Consistency with out-of-scope

- [x] Is offline suggestion strip specified without implying Google cloud/voice candidates?
- [x] Is suggestion **candidate** suppression for password/email/TYPE_NULL/`NO_SUGGESTIONS` covered, with Features toolbar still available?
- [x] Are swipe/voice/translate/GIF/floating keyboard exclusions explicit?
- [x] Is freekeyboard branding (no Google/Gboard marks) consistent across settings and IME chrome?
- [x] Are success criteria for UX still technology-agnostic and measurable?

## Notes

- Reviewer marks `[x]` when the **requirement text** satisfies the criterion.
- Spec updated 2026-11-06: offline suggestions + number row + globe/spacebar language UX are in scope.
- Spec updated 2026-11-07: symbols long-press, NOOP pad, start-input reset, TYPE_NULL keys, shared height metrics.
