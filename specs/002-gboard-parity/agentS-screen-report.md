# Agent S — Gboard functionality screen report

**Date**: 2026-11-07  
**Repo**: freekeyboard  
**Method**: Re-screen every matrix row with `In-scope = yes` / `yes*` against **current code** + unit tests. Prefer code over Agent 3 status. Respect product decisions in `out-of-scope.md` / `parity-audit-2026-11-07.md`.

---

## Verdict

### **PASS_WITH_GAPS**

P0 in-scope typing parity is complete. Spacebar caret swipe is **Done**; glide typing and keyboard resize are **Hard OOS and absent** (correct). Remaining in-scope gaps are P1 TYPE_NULL Cyrillic fallback polish and optional P2 a11y / composing / batch-edit items — none block daily EN+RU typing.

---

## Product-decision confirmation

| Decision | Expected | Code evidence | Result |
|----------|----------|---------------|--------|
| Spacebar L/R swipe → move caret (`A07-007` / `A21-005`) | **In-scope Done** | `SpaceCursorSwipe.kt`; `KeyGrid` space gesture; `KeyboardController.onSpaceCursorSwipe` → `InputConnectionFacade.moveCursorBy` (code-point safe); `SpaceCursorSwipeTest` | **Confirmed Done** |
| Glide / gesture typing across keys | **Hard OOS / absent** | No glide engine, path model, or cross-key swipe-type handler; only exclusion notes (`Models.kt`, `FeaturesMenu`) | **Confirmed absent (good)** |
| Keyboard resize / height scale | **Hard OOS / absent** | No resize handles, height-scale prefs, or drag-to-resize UI; metrics fixed in `GboardMetrics` | **Confirmed absent (good)** |
| Voice / translate / GIF / floating / cloud / Google Search / mic | Hard OOS | `FeaturesMenu` offline-only; no mic/search/GIF/translate/voice APIs | **Confirmed absent (good)** |
| Hold-?123-slide, composing span, TalkBack polish | Optional / not required | Hold-?123 absent; no `setComposing*`; partial `contentDescription` | Correctly non-blocking |

---

## Coverage after rescore

In-scope = `yes` / `yes*` only. Hard OOS rows (`no` / `no*`), including resize `A11-003` / `A14-010`, are **excluded** from Done%.

| Priority | In-scope | Done | Partial | Missing | Incorrect | Done% |
|----------|---------:|-----:|--------:|--------:|----------:|------:|
| **P0** | 75 | 75 | 0 | 0 | 0 | **100%** |
| **P1** | 39 | 38 | 1 | 0 | 0 | **97.4%** |
| **P2** | 12 | 7 | 3 | 2 | 0 | **58.3%** |
| **All** | 126 | 120 | 4 | 2 | 0 | **95.2%** |

vs Agent 3 (stale): Agent 3 reported P0 90.7% / P1 68.3% with Incorrect strip-chrome and several Partials that **current code has closed**. Agent 3 also treated resize as in-scope Missing; product now marks resize **Hard OOS**.

---

## Top remaining in-scope gaps (≤10)

| # | ID | Pri | Status | Severity | Gap |
|---|----|-----|--------|----------|-----|
| 1 | A16-005 | P1 | Partial | **Medium** | TYPE_NULL Cyrillic uses `commitText` fallback when no hardware keycode (`RawKeyCodes` / `sendKeyChar`) — stable but not Gboard-exact raw events |
| 2 | A20-001 | P2 | Partial | Low | Key `contentDescription` present; TalkBack polish incomplete |
| 3 | A20-002 | P2 | Partial | Low | Suggestion chips clickable; chip a11y announcements incomplete |
| 4 | A20-004 | P2 | Missing | Low | Switch Access not verified / not specialized |
| 5 | A21-011 | P2 | Missing | Low | No composing span (`setComposingText`) — **optional / not required** |
| 6 | A21-012 | P2 | Partial | Low | Word delete lacks `beginBatchEdit`/`endBatchEdit` wrapper — behavior OK |

No P0 gaps. No actionable Hard-OOS “gaps” (glide/resize must stay absent).

---

## Hard OOS confirmed absent (good)

| Item | Absent? |
|------|---------|
| Glide / swipe-typing (жест-набор) | **Yes** |
| Keyboard resize / height scale | **Yes** |
| Voice / talk-to-text / mic key | **Yes** |
| Translate | **Yes** |
| GIF / sticker network tabs | **Yes** |
| Floating keyboard | **Yes** |
| Cloud personalization / federated learning UI | **Yes** |
| Google Search toolbar chip | **Yes** |

---

## Agent 3 findings overturned by current code

| Prior Agent 3 | Agent S | Why |
|---------------|---------|-----|
| A10-006 / A16-001 Incorrect (strip chrome on suppress) | **Done** | `KeyboardRoot` gates `SuggestionBar` on `!suppressSuggestions` |
| A07-007 / A21-005 Partial | **Done** | Full wire + unit tests + code-point `moveCursorBy` |
| A14-006/007 Partial (no volume/strength) | **Done** | Prefs + sliders + `KeyFeedback` parameters |
| A12-006/007 Missing | **Done** | `EmojiSearch` + panel field; `emojiFontScale` |
| A15-005 Missing | **Done** | Personal dictionary section in `SettingsScreens` |
| A19-002 Partial (unwired) | **Done** | `LearningPolicy.recordTypedToken` on space / autocorrect path |
| A11-002 Partial (no shrink) | **Done** | ~2/3 `weight(2f)` + rail dock |
| A11-003 / A14-010 Missing as P1 gap | **Hard OOS** | Product removed resize; not a freekeyboard parity failure |

---

## Matrix file hygiene

- `gboard-feature-matrix.md` status cells for contested rows already match Agent S (Done for swipe/suppress/emoji search/etc.; resize Missing + `In-scope=no`).
- Tallies / “Remaining highest-value P1” updated to drop resize-as-gap language.
- Full delta table: `agentS-matrix-rescored.md`.

---

## Return summary (for parent)

| Field | Value |
|-------|--------|
| Verdict | **PASS_WITH_GAPS** |
| P0 Done% | **100%** (75/75) |
| P1 Done% | **97.4%** (38/39) |
| Top 5 gaps | A16-005 Partial; A20-001/002 Partial; A20-004 Missing; A21-011 Missing (optional) |
| Space caret swipe | **Done** |
| Glide typing | **Hard OOS / absent** (not a gap) |
