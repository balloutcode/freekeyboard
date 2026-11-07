# Tasks: IME Core EN/RU

**Input**: Design documents from `/specs/001-ime-core-en-ru/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/

**Tests**: Optional unit tests included for layout/preferences; IME Service paths validated via quickstart.md

**Organization**: Tasks grouped by user story for independent implementation.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: US1–US6 mapping to spec user stories

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Android project scaffold with pinned toolchain

- [x] T001 Create Gradle multi-file skeleton per plan: `settings.gradle.kts`, root `build.gradle.kts`, `gradle/libs.versions.toml`, `gradle/wrapper` (Gradle 9.8.2)
- [x] T002 Create `app/build.gradle.kts` with applicationId `com.freekeyboardballoutcode`, minSdk 31, compileSdk/targetSdk 37, Kotlin 2.4.21, Compose BOM 2026.09.00, AGP 9.5.1, JDK 21 toolchain
- [x] T003 [P] Add AndroidX dependencies in version catalog: Compose Material3, activity-compose 1.13.0, lifecycle 2.11.0, coroutines 1.11.0, DataStore Preferences
- [x] T004 [P] Create package directories under `app/src/main/java/com/freekeyboardballoutcode/` (`ime/`, `settings/`, `data/`, `ui/theme/`) and `app/src/main/res/`
- [x] T005 [P] Add Russian baseline strings in `app/src/main/res/values/strings.xml` and original launcher/IME icons under `res/drawable/` (no Google assets)

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: IME registration, Compose lifecycle host, preferences, theme shell

**CRITICAL**: No user story work until this phase completes

- [x] T006 Implement `ImeLifecycleOwner` helpers and wire Lifecycle/ViewModelStore/SavedState in `app/src/main/java/com/freekeyboardballoutcode/ime/ImeLifecycleOwner.kt` + service base hooks
- [x] T007 Create `FreekeyboardImeService` stub in `app/src/main/java/com/freekeyboardballoutcode/ime/FreekeyboardImeService.kt` returning ComposeView from `onCreateInputView`, lifecycle to RESUMED on show / destroy on hide
- [x] T008 [P] Declare IME service + BIND_INPUT_METHOD + `meta-data` in `app/src/main/AndroidManifest.xml` per `contracts/ime-manifest.md`
- [x] T009 [P] Add `app/src/main/res/xml/method.xml` subtypes `en-US` / `ru-RU` and settingsActivity attribute
- [x] T010 [P] Implement `UserPreferencesRepository` with DataStore in `app/src/main/java/com/freekeyboardballoutcode/data/UserPreferencesRepository.kt` (theme, languages, last language, recent emoji)
- [x] T011 [P] Implement Material3 Light/Dark/OLED Dark schemes in `app/src/main/java/com/freekeyboardballoutcode/ui/theme/` (`Color.kt`, `Theme.kt`, `Type.kt`)
- [x] T012 Implement `InputConnectionFacade` + `EditorInfoInterpreter` in `app/src/main/java/com/freekeyboardballoutcode/ime/input/` per `contracts/ime-input-connection.md`
- [x] T013 Override `onEvaluateFullscreenMode()` to return false and handle configuration changes in `FreekeyboardImeService.kt`
- [x] T014 Create empty `KeyboardRoot` Compose host applying selected theme in `app/src/main/java/com/freekeyboardballoutcode/ime/ui/KeyboardRoot.kt`

**Checkpoint**: App installs; IME appears in system settings; enabling shows themed empty Compose keyboard without crash

---

## Phase 3: User Story 1 - Enable and Type (Priority: P1) MVP

**Goal**: Soft keyboard inserts basic Latin characters, space, enter, backspace

**Independent Test**: quickstart S1 subset with EN only

- [x] T015 [US1] Define `KeyAction` and EN QWERTY `KeyDefinition` layout data in `app/src/main/java/com/freekeyboardballoutcode/ime/keyboard/layouts/EnQwerty.kt`
- [x] T016 [US1] Implement `KeyGrid` composable in `app/src/main/java/com/freekeyboardballoutcode/ime/ui/KeyGrid.kt`
- [x] T017 [US1] Implement `KeyboardController` commit/backspace/space/enter routing via `InputConnectionFacade` in `app/src/main/java/com/freekeyboardballoutcode/ime/keyboard/KeyboardController.kt`
- [x] T018 [US1] Wire LETTERS layer into `KeyboardRoot.kt` and `FreekeyboardImeService` start/finish input callbacks
- [x] T019 [P] [US1] Add unit tests for EN layout key → output mapping in `app/src/test/java/com/freekeyboardballoutcode/ime/keyboard/EnQwertyTest.kt`

**Checkpoint**: Can enable IME and type English in a text field

---

## Phase 4: User Story 2 - EN/RU Language Switch (Priority: P1)

**Goal**: Russian ЙЦУКЕН layout + globe toggle + Space long-press picker

**Independent Test**: quickstart S1 language switch portion

- [x] T020 [P] [US2] Add RU ЙЦУКЕН layout in `app/src/main/java/com/freekeyboardballoutcode/ime/keyboard/layouts/RuJcuiken.kt`
- [x] T021 [US2] Persist/load `lastActiveLanguage` and enabled languages via `UserPreferencesRepository`
- [x] T022 [US2] Implement globe key toggle and Space long-press language picker UI in `ime/ui/` (new `LanguagePicker.kt`)
- [x] T023 [US2] Integrate language switching into `KeyboardController` / `KeyboardRoot` without swipe gestures
- [x] T024 [P] [US2] Unit tests for language toggle constraints (cannot disable last language) in `app/src/test/java/com/freekeyboardballoutcode/data/UserPreferencesTest.kt`

**Checkpoint**: EN↔RU switch works; last language restored after hide/show

---

## Phase 5: User Story 3 - Symbols, Shift, Long-Press (Priority: P1)

**Goal**: Symbols layer, shift/caps, variant popup

**Independent Test**: quickstart S2

- [x] T025 [P] [US3] Add symbols/numbers layout in `app/src/main/java/com/freekeyboardballoutcode/ime/keyboard/layouts/Symbols.kt`
- [x] T026 [US3] Implement ShiftState transitions in `KeyboardController.kt` (Gboard: tap ONCE; double-tap/long-press CAPS)
- [x] T027 [US3] Implement `VariantPopup.kt` long-press selection in `ime/ui/VariantPopup.kt`
- [x] T028 [US3] Wire layer keys LETTERS ↔ SYMBOLS in `KeyboardRoot.kt`
- [x] T029 [P] [US3] Unit tests for shift once vs caps in `app/src/test/java/com/freekeyboardballoutcode/ime/keyboard/ShiftStateTest.kt`

**Checkpoint**: Digits/punctuation, capitals, and variants insert correctly

---

## Phase 6: User Story 4 - Portrait and Landscape (Priority: P1)

**Goal**: Usable layouts in both orientations; no fullscreen extract

**Independent Test**: quickstart S3

- [x] T030 [US4] Make `KeyGrid` / `KeyboardRoot` responsive to configuration width/height in landscape
- [x] T031 [US4] Verify and enforce fullscreen evaluation false on config change in `FreekeyboardImeService.kt`
- [x] T032 [US4] Preserve language, layer, and shift across `onConfigurationChanged` / input view recreate
- [x] T033 [US4] Manually validate quickstart S3 on emulator API 31+; record result in PR notes

**Checkpoint**: Landscape typing usable without extract UI

---

## Phase 7: User Story 5 - Emoji Panel (Priority: P2)

**Goal**: Full offline emoji panel with categories and recents

**Independent Test**: quickstart S4

- [x] T034 [US5] Implement `EmojiPanel.kt` with category tabs using platform emoji in `ime/ui/EmojiPanel.kt`
- [x] T035 [US5] Implement `RecentEmojiStore` upsert/trim in `app/src/main/java/com/freekeyboardballoutcode/data/RecentEmojiStore.kt`
- [x] T036 [US5] Wire emoji key and ABC return to previous letters language in `KeyboardRoot.kt` / `KeyboardController.kt`
- [x] T037 [US5] Commit emoji via Unicode `commitText` through `InputConnectionFacade`
- [x] T038 [P] [US5] Unit tests for recent emoji cap behavior in `app/src/test/java/com/freekeyboardballoutcode/data/RecentEmojiStoreTest.kt`

**Checkpoint**: Emoji insert + recents + return to letters works offline

---

## Phase 8: User Story 6 - Themes and Russian Settings (Priority: P2)

**Goal**: Settings Activity in Russian; Light/Dark/OLED themes apply to keyboard

**Independent Test**: quickstart S5

- [x] T039 [US6] Create `SettingsActivity` Compose UI in `app/src/main/java/com/freekeyboardballoutcode/settings/SettingsActivity.kt` and `settings/ui/SettingsScreens.kt` (all copy Russian)
- [x] T040 [US6] Theme selection writes `ThemePreference` and keyboard recomposes with Light/Dark/OLED (pure black) tokens
- [x] T041 [US6] Link settings from `method.xml` / IME and ensure Manifest Activity declaration
- [x] T042 [US6] Optional language enable toggles for EN/RU in settings respecting last-language invariant
- [x] T043 [P] [US6] Instrumentation or screenshot-free smoke test launching settings in `app/src/androidTest/.../SettingsActivityTest.kt`

**Checkpoint**: Russian settings change themes; keyboard reflects choice

---

## Phase 9: Polish & Cross-Cutting

- [x] T044 [P] Update root `README.md` with build/run/enable IME instructions (Russian or bilingual as appropriate)
- [x] T045 Ensure no Gboard/Google branding strings/assets remain in `app/src/main/`
- [x] T046 Run full `quickstart.md` scenarios S1–S5 on API 31+ emulator and fix blockers
- [x] T047 [P] Add `metadata/` or fastlane structure stub for future F-Droid (description placeholders only)
- [x] T048 Confirm no swipe detectors; no Google cloud candidates (Compose offline `SuggestionBar` is in scope — supersedes early “no suggest UI” note)

---

## Dependencies & Execution Order

### Phase Dependencies

- Setup → Foundational → US1 → US2 → US3 → US4 → US5 → US6 → Polish
- US2–US4 depend on US1 keyboard controller/grid existing
- US5–US6 can start after Foundational+US1; practically after US2 for language restore from emoji

### Parallel Opportunities

- T003–T005 in Setup
- T008–T011 in Foundational
- T020 with preference tests; T025 with shift tests; T038 with emoji UI

### MVP

Complete Phases 1–3 (Setup, Foundational, US1) for first demoable IME; then US2–US4 for P1 complete; then US5–US6.

## Notes

- Offline suggestion strip is in scope for the shipped IME core (see updated `spec.md` FR-016); personal learning dictionary may still be a later feature
- Do not implement swipe typing / Google cloud services / voice / translate / floating keyboard
- Feature is implemented; SDD as-built sync 2026-11-07. Use `/speckit-converge` when extending further.

---

## Phase N: Convergence (2026-11-06 QA)

**Purpose**: Close gaps found during full functional + human-emulation testing vs Gboard-like expectations and updated spec.

- [x] C001 Add globe `LANGUAGE` key to EN/RU/symbols bottom rows; hide when fewer than two languages enabled (`LayoutOptions`, layouts)
- [x] C002 Spacebar label shows active language Russian display name (`KeyGrid` + `InputLanguage.displayNameRu`)
- [x] C003 Long-press Space opens language picker; globe toggles when two languages enabled (`KeyboardRoot` / `KeyboardController`)
- [x] C004 Clamp `lastActiveLanguage` to enabled set when preferences apply (`KeyboardController.applyPreferences`)
- [x] C005 Emoji panel: category tabs on top, grid, 48dp bottom **ABC** return (Gboard-like close affordance)
- [x] C006 Refresh Light/Dark/OLED palettes and verify on emulator screenshots
- [x] C007 Human-emulation smoke: `Hello` + globe → `да` + emoji insert + language picker
- [x] C008 Verify long-press accent insertion and double-space → `. ` on emulator
- [x] C009 Reconcile `spec.md`, `plan.md`, `data-model.md`, `contracts/ui-keyboard.md`, `quickstart.md` with shipped behavior (suggestions, number row, preferences)
- [x] C010 Caps Lock Gboard parity: double-tap / long-press Shift → `CAPS`; single tap unlocks; unit tests `ShiftStateTest`
- [x] C011 Dark theme = neutral charcoal (no blue slate); OLED remains pure black
- [x] C012 Settings Activity applies Light/Dark/OLED to scaffold + system bars
- [x] C013 IME bottom inset ≈ Gboard screen-edge → spacebar (~54 dp / 108 px @ 320 dpi)
- [x] C014 Emulator functional suite 2026-11-06 evening: Hello+RU, Caps, symbols, picker, suggestions, double-space, backspace hold, settings themes — 0 blockers; SDD artifacts updated

---

## Phase N+1: Convergence (2026-11-07 code-review / as-built)

**Purpose**: Land post-review hardening already present in code and refresh Spec Kit artifacts so docs match the repository.

- [x] C015 Symbols Shift long-press toggles `symbolsAlt` (`KeyboardController.onShiftLongPress`)
- [x] C016 RU number-row 11th slot is `KeyAction.NOOP` (`LayoutOptions`); empty COMMIT guarded
- [x] C017 `onStartInput` resets layer to LETTERS / clears symbolsAlt / dismisses picker
- [x] C018 `RawKeyCodes` + TYPE_NULL key-event path for Space/punctuation/letters
- [x] C019 `WordDelete` code-point word backspace helper wired through `InputConnectionFacade`
- [x] C020 Shared `GboardMetrics.letterLayerContentHeight` for emoji panel + first-frame insets (landscape + number row + suggestion strip)
- [x] C021 Robust IME nav chrome tint (class-name hints + bottom-strip fallback)
- [x] C022 Suggestion suppression via `EditorInfoInterpreter.suppressSuggestions`
- [x] C023 Unit tests: LayoutOptions, RawKeyCodes, WordDelete, EditorInfoInterpreter, KeyboardControllerState, GboardMetrics
- [x] C024 As-built sync of `spec.md`, `plan.md`, `data-model.md`, contracts, `research.md`, `quickstart.md`, checklists, constitution 1.0.1
- [x] C025 Polish pass: plan doc tree lists `spec.md`/`checklists/`; Key Entities + SC-009; SYMBOLS Shift clarification aligned with long-press

---

## Phase N+2: Convergence (2026-11-08 privacy / Bugbot as-built)

**Purpose**: Keep 001 SDD aligned with cross-cutting 003/004 behavior without rewriting earlier task IDs.

- [x] C026 FR-026–029: toolbar-on-suppress; multiline Enter; password space guards (see also `specs/004-privacy-hardening/`)
- [x] C027 Contracts + data-model + quickstart synced (NUMBER layer, clipboard opt-in refs, caret DPAD, backup off)
- [x] C028 Constitution 1.1.0 privacy/storage amendments referenced from 001 plan
