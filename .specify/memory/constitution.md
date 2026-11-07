<!--
Sync Impact Report
- Version change: 1.0.1 → 1.1.0
- Modified principles: II (privacy storage / backup / clipboard clarifications)
- Added sections: Product Constraints — sensitive local data
- Removed sections: N/A
- Follow-up TODOs: optional EncryptedDataStore for clipboard (deferred)
-->

# freekeyboard Constitution

## Core Principles

### I. FOSS-First / F-Droid Inclusion
Every dependency, asset, build tool, and shipped binary MUST be Free/Libre
Open Source Software compatible with distribution under GPL-3.0 and F-Droid
inclusion policy. Proprietary libraries (including Google Play Services,
Firebase, Crashlytics, and closed analytics/ad SDKs) are forbidden. Prebuilt
binaries MUST come only from trusted FLOSS-capable sources allowed by F-Droid
(e.g. Maven Central / Google Maven AndroidX with free licenses). Rationale:
F-Droid rebuildability and user freedom are non-negotiable product goals.

### II. Privacy and Offline-by-Default
The keyboard MUST work fully offline for core typing. No telemetry, crash
reporting to third parties, or cloud sync of typed content. Network access
MUST NOT be required for input, emoji, themes, or settings. Any future
optional network feature MUST be explicit opt-in, disclosed in UI, and
compatible with F-Droid consent rules.

Sensitive local data (clipboard history, rejected-suggestion tokens, personal
dictionary) MUST NOT leave the device via Android Backup / device transfer:
`allowBackup` MUST stay false (or equivalent exclusion rules). Clipboard
history MUST be opt-in, honor platform-sensitive clip flags (API 33+), avoid
capture over password / suppressed / incognito fields, and wipe when disabled.
Password and other suggestion-suppressed fields MUST NOT receive autocorrect
or double-space mutation. Rationale: an IME sees all text; trust requires
local-only defaults and careful persistence.

### III. Gboard UX Parity Without Brand or Asset Copying
User-facing behavior and visual design SHOULD match the contemporary Gboard
experience for covered features (layouts, gestures except swipe, emoji,
themes, settings flow). The product MUST NOT use Google/Gboard names, logos,
icons, proprietary assets, or trademarks. Branding is freekeyboard only;
applicationId is `com.freekeyboardballoutcode`. Rationale: familiar UX without
legal/IP risk or F-Droid trademark rejection.

### IV. Language and Locale Scope
Settings and in-app UI strings MUST be Russian. Input languages for the
initial product scope are English and Russian only. Additional input
languages require a new feature spec. Rationale: clear MVP boundary and
localized ownership for the primary audience.

### V. Interaction and Form-Factor Boundaries
Swipe-typing MUST NOT be implemented. Target form factor is phone, with both
portrait and landscape orientations supported. Landscape MUST NOT force
fullscreen extract IME mode for the soft keyboard. Tablets and foldables are
out of scope until specified. Rationale: agreed product cuts reduce
complexity while preserving real phone usage.

### VI. Modern Android Toolchain and IME Architecture
Minimum Android API is 31. Implementation uses current stable Kotlin, Jetpack
Compose, and Android Gradle Plugin pins recorded in feature plans. Soft
keyboard UI MAY use Compose inside `InputMethodService` only when synthetic
Lifecycle / ViewModelStore / SavedState owners are provided and lifecycle is
advanced to RESUMED while the IME window is visible. Release builds SHOULD
enable R8 minify + resource shrink with keep rules for IME/settings entry
points. Rationale: Compose without correct owners freezes recomposition;
release hardening raises the bar for static analysis without changing UX.

### VII. Spec-Driven Delivery
Work proceeds in bounded features under Spec Kit: constitution → specify →
clarify (when ambiguous) → plan → checklist → tasks → analyze → implement →
converge. Specs describe WHAT/WHY without stack detail; plans own HOW.
Implementation MUST NOT begin until analyze is clean for that feature (or
exceptions are explicitly documented). Rationale: keeps a large IME project
reviewable and F-Droid-ready.

## Product Constraints

- License: GPL-3.0-or-later for project code; third-party licenses MUST be
  compatible and documented.
- No Google cloud suggestion/voice. The IME core MAY ship an offline FLOSS
  suggestion strip (completions / next-word) with static bundled dictionaries.
  Personal learning dictionaries and cloud personalization remain later features
  and MUST stay on-device when added.
- Emoji panel, light/dark/OLED themes, offline suggestions, and EN/RU layouts
  are in-scope for the IME core feature set as specified per feature directory.
- Personal dictionary data MUST remain on-device.
- Inline autofill (API 30+) hosts platform password-manager chips only; the
  app MUST NOT implement an AutofillService or log credential content.
- Clipboard history: local-only MVP; default **off**; unpinned TTL 1h; pinned
  TTL ≤ 7 days; per-entry delete; clear-on-disable.

## Development Workflow

- Feature directories under `specs/` are the source of intended behavior for
  delivery; use flow-forward persistence (completed feature dirs remain
  historical records) unless Governance amends this.
- Prefer small, reviewable features over monolithic “entire keyboard” specs.
- Tests: unit tests for layout/input logic; instrumentation or manual
  quickstart scenarios for IME enablement and typing paths documented in
  each feature `quickstart.md`.
- Complexity beyond constitution gates MUST be justified in the feature plan
  Complexity Tracking section.

## Governance

This constitution supersedes informal habits and ad-hoc agent prompts.
Amendments require: (1) documented rationale, (2) version bump per SemVer
(MAJOR for incompatible principle changes, MINOR for new principles/sections,
PATCH for clarifications), (3) update of `Last Amended` date, (4) review that
active feature plans still comply or are revised. All PRs and Spec Kit
analyze/implement steps MUST verify compliance with Core Principles.
Unjustified violations are ERROR gates in planning.

**Version**: 1.1.0 | **Ratified**: 2026-11-06 | **Last Amended**: 2026-11-08
