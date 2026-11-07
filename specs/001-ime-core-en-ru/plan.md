# Implementation Plan: IME Core EN/RU

**Branch**: `001-ime-core-en-ru` | **Date**: 2026-11-06 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/001-ime-core-en-ru/spec.md`

## Summary

Deliver a system soft keyboard (freekeyboard) with Gboard-like EN/RU layouts, symbols, optional number row, long-press variants, globe language key + space long-press picker, emoji panel (bottom ABC), offline suggestion strip, Russian settings, and Light/Dark/OLED themes on Android 12+ phones (portrait + landscape), without swipe-typing or Google cloud services. Technically: single Android app module using Kotlin + Jetpack Compose hosted inside `InputMethodService` with correct synthetic lifecycle owners, Material3 theming, and DataStore-backed preferences.

**Maintenance note (2026-11-06)**: Spec/plan/tasks reconciled to the shipped surface after human-emulation QA (typing, language switch, symbols, emoji ABC, long-press accents, double-space period, themes). Intentional exclusions remain: swipe/glide, voice, translate, GIF/stickers, floating keyboard, Google services.

**QA refresh (2026-11-06 evening)**: Emulator functional suite passed (Hello + RU `да`, Caps double-tap/hold, symbols, picker, suggestions, double-space, backspace hold, settings Light/Dark/OLED). Caps Lock is Gboard double-tap/long-press (not single-tap cycle). Dark theme is neutral charcoal; settings Activity follows the same three themes; IME bottom inset ≈ Gboard (~54 dp).

**As-built sync (2026-11-07)**: Specs/plan/contracts reconciled to post code-review code: Symbols Shift long-press toggles alt page; RU number-row `NOOP` pad; `onStartInput` resets to Letters; `RawKeyCodes` for TYPE_NULL; suggestion suppression by `EditorInfo`; shared `GboardMetrics.letterLayerContentHeight` for emoji + first-frame insets; robust IME nav tint; unit coverage for LayoutOptions / RawKeyCodes / WordDelete / controller state.

**As-built sync (2026-11-08)**: Privacy/Bugbot hardening lives primarily in [004](../004-privacy-hardening/); 001 contracts updated for toolbar-on-suppress, multiline Enter, caret-swipe DPAD fallback, and password space guards. Constitution **1.1.0**.

## Technical Context

**Language/Version**: Kotlin 2.4.21; JDK toolchain 21

**Primary Dependencies**: Android Gradle Plugin 9.5.1; Gradle 9.8.2; Jetpack Compose BOM 2026.09.00 (Compose UI 1.12.1, Material3 1.4.0); androidx.activity:activity-compose 1.13.0; androidx.lifecycle 2.11.0; kotlinx-coroutines 1.11.0; AndroidX DataStore Preferences (latest stable compatible with BOM/lifecycle pins)

**Storage**: DataStore Preferences for theme, key borders, languages, recent emoji, number row, emoji key, key feedback, long-press, suggestion toggles, auto-cap, double-space

**Testing**: JUnit unit tests on JVM for layouts, LayoutOptions, shift/controller state, preferences, AutoCapitalizer, EditorInfoInterpreter, RawKeyCodes, WordDelete, GboardMetrics, suggestions/profanity; manual IME quickstart + human-emulation on emulator/device

**Target Platform**: Android phone, minSdk 31, compileSdk 37, targetSdk 37

**Project Type**: mobile-app (Android IME + settings Activity)

**Performance Goals**: Key tap to text commit path under ~16ms on mid-range phones for single character; keyboard show without multi-second jank; 60fps-capable Compose animations when lifecycle is RESUMED

**Constraints**: FLOSS-only / F-Droid; no GMS; offline; no swipe-typing; offline suggestions only; landscape without fullscreen extract; Compose-in-IME lifecycle MUST reach RESUMED while visible; GPL-3.0

**Scale/Scope**: One app module; two input languages; three themes; emoji panel; offline suggestions; Gboard-like preference surface (excluding Google-only features); Russian settings

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle | Status | Notes |
|-----------|--------|-------|
| I. FOSS-First / F-Droid | PASS | AndroidX + Kotlin only; no GMS |
| II. Privacy / Offline | PASS | Local DataStore; no network |
| III. UX parity without brand copy | PASS | freekeyboard branding; original assets |
| IV. RU UI / EN+RU input | PASS | Spec FR-004/018 |
| V. No swipe; phone + landscape | PASS | Fullscreen extract disabled |
| VI. API 31+ / Compose IME lifecycle | PASS | Documented in research |
| VII. Spec-driven delivery | PASS | This plan follows Spec Kit |

**Post-design re-check**: PASS — structure remains single module; complexity of synthetic LifecycleOwner is required and justified below.

## Project Structure

### Documentation (this feature)

```text
specs/001-ime-core-en-ru/
├── spec.md              # WHAT/WHY — primary feature contract
├── plan.md              # HOW — technical design
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
├── checklists/
└── tasks.md
```

### Source Code (repository root)

```text
settings.gradle.kts
build.gradle.kts
gradle/libs.versions.toml
gradle/wrapper/
app/
├── build.gradle.kts
├── src/main/
│   ├── AndroidManifest.xml
│   ├── java/com/freekeyboardballoutcode/
│   │   ├── ime/
│   │   │   ├── FreekeyboardImeService.kt
│   │   │   ├── ImeLifecycleOwner.kt
│   │   │   ├── input/
│   │   │   │   ├── InputConnectionFacade.kt
│   │   │   │   ├── EditorInfoInterpreter.kt
│   │   │   │   ├── AutoCapitalizer.kt
│   │   │   │   ├── RawKeyCodes.kt
│   │   │   │   ├── WordDelete.kt
│   │   │   │   └── KeyFeedback.kt
│   │   │   ├── keyboard/
│   │   │   │   ├── KeyboardController.kt
│   │   │   │   ├── LayoutOptions.kt
│   │   │   │   ├── KeyAction.kt (+ KeyDefinition / rows)
│   │   │   │   └── layouts/ (EnQwerty, RuJcuiken, Symbols)
│   │   │   ├── suggest/
│   │   │   │   ├── SuggestionEngine.kt
│   │   │   │   ├── SuggestionDictionary.kt
│   │   │   │   ├── NextWordModel.kt
│   │   │   │   └── ProfanityLexicon.kt
│   │   │   └── ui/
│   │   │       ├── KeyboardRoot.kt
│   │   │       ├── KeyGrid.kt
│   │   │       ├── GboardMetrics.kt
│   │   │       ├── SuggestionBar.kt
│   │   │       ├── VariantPopup.kt
│   │   │       ├── KeyPressOverlay.kt
│   │   │       ├── LanguagePicker.kt
│   │   │       ├── KeyboardIcons.kt
│   │   │       └── EmojiPanel.kt
│   │   ├── settings/
│   │   │   ├── SettingsActivity.kt
│   │   │   └── ui/SettingsScreens.kt
│   │   ├── data/
│   │   │   ├── Models.kt
│   │   │   ├── UserPreferencesRepository.kt
│   │   │   └── RecentEmojiStore.kt
│   │   └── ui/theme/
│   │       ├── Theme.kt
│   │       ├── Color.kt
│   │       └── Type.kt
│   └── res/
│       ├── xml/method.xml
│       ├── values/strings.xml (Russian)
│       └── drawable/ (original icons)
└── src/test/java/...   (layouts, shift, prefs, suggest, RawKeyCodes, WordDelete, …)
└── src/androidTest/java/...
```

**Structure Decision**: Single `app` module. Offline suggestion engine lives under `ime/suggest/` (no separate module). Personal learning dictionary may still become a later feature without splitting modules until needed.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| Custom LifecycleOwner / ViewModelStoreOwner / SavedStateRegistryOwner on InputMethodService | Compose requires ViewTree owners; Service is not ComponentActivity | XML/View-only keyboard would abandon agreed Compose stack and slow Gboard-like UI iteration |
