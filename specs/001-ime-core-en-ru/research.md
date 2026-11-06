# Research: IME Core EN/RU

## Decision: Compose UI inside InputMethodService

**Decision**: Host keyboard and emoji UI via `ComposeView` returned from `onCreateInputView()`, with the IME service implementing `LifecycleOwner`, `ViewModelStoreOwner`, and `SavedStateRegistryOwner`, advancing lifecycle to `RESUMED` while the IME window is visible and down on hide/destroy.

**Rationale**: Project stack mandates Jetpack Compose. Google Issue Tracker and community IME samples show animations/recomposition freeze if lifecycle stays below `STARTED`/`RESUMED` because the window-scoped recomposer pauses its frame clock.

**Alternatives considered**:
- XML/`KeyboardView` — simpler lifecycle, poor match for Material3 Gboard-like UI and rejected by stack choice.
- Compose only in Settings Activity + Views for IME — dual UI stacks, slower parity work.

## Decision: Disable fullscreen extract mode on phones

**Decision**: Override `onEvaluateFullscreenMode()` to return `false` (and call `updateFullscreenMode()` on config changes) so landscape keeps a bottom soft keyboard.

**Rationale**: Spec requires landscape without fullscreen extract; default `InputMethodService` behavior often enables fullscreen in landscape.

**Alternatives considered**: Custom extract UI — more work, worse Gboard-like UX for phones.

## Decision: Subtypes via `languageTag`

**Decision**: Declare subtypes in `res/xml/method.xml` with `android:languageTag="en-US"` and `android:languageTag="ru-RU"`, `imeSubtypeMode="keyboard"`, and `settingsActivity` pointing to settings.

**Rationale**: Current Android IME guide prefers BCP-47 `languageTag` over legacy `imeSubtypeLocale`.

**Alternatives considered**: Single subtype with in-app only language switch — weaker system IME switcher integration.

## Decision: Text commit path

**Decision**: Prefer `commitText` for normal editors; code-point-safe deletes for backspace; for `TYPE_NULL`, map printable ASCII via `RawKeyCodes` → `sendKeyEvent` down/up (with `META_SHIFT_ON` when needed); unmappable glyphs fall back to `commitText`. Emoji via Unicode `commitText`.

**Rationale**: Matches Android `InputConnection` guidance; TYPE_NULL editors often ignore `commitText` for Space/punctuation if only string commits are used.

**Alternatives considered**: Always raw `KeyEvent` — breaks many modern editors. `keyCodeFromString("KEYCODE_.")` — unreliable for punctuation; replaced by explicit `RawKeyCodes` table.

## Decision: Preferences storage

**Decision**: AndroidX DataStore Preferences for theme, languages, last language, recent emoji list (capped), and the full typing/suggestion preference surface.

**Rationale**: FLOSS, async, survives process death better than ad-hoc files; no Room needed for 001.

**Alternatives considered**: SharedPreferences — older API; Room — overkill for key-value prefs.

## Decision: Toolchain pins

**Decision**: Kotlin 2.4.21, AGP 9.5.1, Gradle 9.8.2, Compose BOM 2026.09.00, compile/targetSdk 37, minSdk 31, JDK 21.

**Rationale**: Latest stables agreed in product discussion; AGP 9.5 supports API 37; Compose 1.12 needs compileSdk ≥ 37; AGP requires Gradle ≥ 9.6.0 and JDK ≥ 17.

**Alternatives considered**: Preview Kotlin 2.5 / Compose 1.13 RC — rejected for F-Droid/production stability.

## Decision: Offline suggestion strip in 001 (Compose, not CandidatesView)

**Decision**: Ship an offline Compose `SuggestionBar` (completions + optional next-word + optional offensive filter) driven by `ime/suggest/*`. Do **not** use framework `onCreateCandidatesView`. Suppress strip based on `EditorInfo` (password, TYPE_NULL, email, URI, `NO_SUGGESTIONS`, non-text).

**Rationale**: Spec FR-016 brought suggestions into IME core; Compose strip matches Gboard-like chrome without the legacy candidates view API. Personal learning dictionary remains a later feature.

**Alternatives considered**: Defer all suggestions to feature 002 — rejected after product/QA expanded FR-016. Empty candidates bar — visual noise without value.

## Decision: Shared letter-layer height metrics

**Decision**: Centralize portrait/landscape + number-row + suggestion-strip height in `GboardMetrics.letterLayerContentHeight` and reuse for emoji panel height and first-frame inset estimates.

**Rationale**: Prevents emoji open jump and under-estimated first-frame insets when number row is enabled or device is landscape.

**Alternatives considered**: Hard-coded 4-row portrait-only emoji height — caused measurable jumps.

## Decision: Start-input layer reset

**Decision**: `onStartInput` / `onStartInputView` reset to LETTERS (clear symbolsAlt, dismiss picker, Shift OFF); preserve language.

**Rationale**: Avoids “stuck” Symbols/Emoji when switching editors; matches user expectation that a new field opens on letters.
