# Agent 2 implementation log — Gboard parity

**Date**: 2026-11-07  
**Constraint compliance**: No glide/voice/translate/GIF/floating/cloud; no Google brand; no git commit/push.

## Spec Kit

Created `specs/002-gboard-parity/{spec,plan,tasks}.md` for this feature slice (001 remains historical baseline; 002 owns parity deltas).

## P0 results

| ID | Result | Notes |
|----|--------|-------|
| A07-007 / A21-005 | **Done** | Spacebar horizontal swipe moves caret; tap=space; LP=language picker. Shared helpers in `SpaceCursorSwipe`. |
| A06-006 | **Done** | Expanded RU long-press variants (ё/ъ + acute / neighbors). |
| A10-010 | **Done** | Expanded RU dict + frequency-aware ranking; `прив` → `привет` covered by unit test. |
| A19-002 | **Done** | `LearningPolicy` gates password / no-learn / suppress fields; `NoOpLearningStore` today. |

## P1 closed this pass

| ID | Result | Delta vs Gboard |
|----|--------|-----------------|
| A04-006 | Done | Pref «Ряд цифр в паролях» |
| A10-007 | Done (MVP) | Long-press chip rejects locally (no drag-to-trash UI) |
| A11-001 | Done (MVP) | Offline features grid; no mic/search |
| A11-002 | Done (MVP) | Shrink + side rail flip/expand |
| A11-004 | Done | Theme cycle from features menu |
| A13-001..003 | Done (MVP) | Local history, pin, 1h unpinned TTL; no screenshots/sync |
| A15-003/004 | Improved | New prefs + clipboard toggle in settings |
| A16-006 | Done | Number/phone digit pad layer |
| A19-001 | Done | Incognito icon when `IME_FLAG_NO_PERSONALIZED_LEARNING` |

## Key files changed

- `ime/ui/KeyGrid.kt`, `KeyboardRoot.kt`, `SuggestionBar.kt`, `FeaturesMenu.kt`, `ClipboardPanel.kt`
- `ime/keyboard/KeyboardController.kt`, `layouts/{RuJcuiken,NumberPad}.kt`
- `ime/input/{SpaceCursorSwipe,LearningPolicy,InputConnectionFacade,EditorInfoInterpreter}.kt`
- `ime/suggest/{SuggestionDictionary,SuggestionEngine}.kt`
- `data/{Models,UserPreferencesRepository,ClipboardHistoryStore,RejectedSuggestionsStore}.kt`
- `ime/FreekeyboardImeService.kt`, `settings/ui/SettingsScreens.kt`, `res/values/strings.xml`
- Tests under `app/src/test/...`

## Build / tests

- `:app:assembleDebug` — **SUCCESS**
- `:app:testDebugUnitTest` — **SUCCESS** (71 tests)
- JAVA_HOME used: Android Studio JBR (`C:\Program Files\Android\Android Studio\jbr`)

## 001 regressions intentionally preserved

Caps double-tap/long-press, dark charcoal, settings theme, ~54dp insets, TYPE_NULL RawKeyCodes, RU NOOP pad, suggestion suppress, onStartInput layer reset, symbols Shift long-press.

## Manual quickstart additions

See updates intended for scenarios: space swipe; RU LP ё; number field pad; features menu; one-hand; clipboard; reject suggestion; password number-row pref; incognito flag host.
