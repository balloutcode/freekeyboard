# Agent 3 — Adversarial Gboard parity audit report

**Date**: 2026-11-07  
**Auditor**: Agent 3  
**Production code changes**: none  
**Git commit/push**: none  

## Verdict: **PASS_WITH_GAPS**

Agent 2’s P0 slice largely exists in code with unit-test evidence, hard OOS did not land, and emulator smoke confirms core chrome (EN/RU, symbols, dark charcoal, landscape).  

**Not PASS**: strip-suppression acceptance is **Incorrect** (chrome still mounts on password/email/URI/NO_SUGGESTIONS), several Agent 2 “Done” rows are MVP/Partial without live proof, and research artifacts (settings inventory / surfaces map) are **stale**.  

**Not FAIL**: no crash/security blockers found; no glide/voice/translate/GIF/floating/cloud; P0 Done% still ~91% after adversarial rescore.

---

## Coverage tables

Source: `agent3-matrix-rescored.md` (128 In-scope rows).

### By priority

| Priority | N | Done | Partial | Missing | Incorrect | Done% |
|----------|--:|-----:|--------:|--------:|----------:|------:|
| P0 | 75 | 68 | 5 | 0 | 2 | **90.7%** |
| P1 | 41 | 28 | 9 | 4 | 0 | **68.3%** |
| P2 | 12 | 3 | 4 | 5 | 0 | **25.0%** |
| **Total** | **128** | **99** | **18** | **9** | **2** | **77.3%** |

### Agent 2 claim check

| Claim | Agent 3 |
|-------|---------|
| P0 5/5 Done (swipe, RU variants, RU suggest, learn gate) | **Swipe Partial** (unit only); variants/suggest **Done**; learn **Partial** (unwired) |
| ~10–12 P1 closed | Code present; **several downgraded Partial** (one-hand, features, reject UX) |
| Resize A11-003 / A14-010 Missing | **Confirmed Missing** |
| assembleDebug + 71 unit tests green | **Not re-run this audit**; test files present and consistent with claims — treat as **unverified this pass** |

---

## Top 20 remaining gaps

| # | ID | Gap | Pri | Size | Notes |
|---|----|-----|-----|------|-------|
| 1 | A10-006 / A16-001 | Hide **entire** suggestion/toolbar strip on suppress fields | P0 | **S** | Gate `KeyboardRoot` on `suppressSuggestions`, not only empty chips |
| 2 | A11-003 / A14-010 | Keyboard resize + persist + reset | P1 | **L** | High 001 inset regression risk |
| 3 | A07-007 / A21-005 | Prove space cursor swipe live + code-point caret | P0 | **M** | Add instrumentation / manual QA |
| 4 | A11-002 | True one-hand shrink (~2/3 width) + dock animation | P1 | **M** | Today: 44dp rail only |
| 5 | A10-007 | Drag-to-trash reject chrome | P1 | **M** | LP reject exists |
| 6 | A19-002 | Wire `LearningPolicy.recordTypedToken` into commit path | P0 | **S** | Prevent future bypass |
| 7 | A12-001 / A12-008 | Emoji entry when key hidden; live panel QA | P0/P1 | **S** | FeaturesMenu fallback + prefs check |
| 8 | A14-006 / A14-007 | Sound volume + vibration strength | P1 | **M** | On/off only |
| 9 | A15-003 | Settings IA / inventory refresh vs Gboard 18.5.6 | P1 | **M** | Agent 1 dump incomplete; inventory stale |
| 10 | A16-005 | TYPE_NULL Cyrillic polish | P1 | **S** | commitText fallback OK |
| 11 | A12-006 | Offline emoji search | P2 | **M** | |
| 12 | A12-007 | Emoji size slider | P2 | **S** | |
| 13 | A03-007 | Hold ?123 slide-insert | P2 | **M** | Optional |
| 14 | A15-005 | Personal dictionary screen | P2 | **L** | Product-gated |
| 15 | A20-001..004 | TalkBack / font scale / Switch Access | P2 | **L** | |
| 16 | A21-011 | Composing span | P2 | **M** | Optional |
| 17 | A11-001 | Features menu visual + a11y + reliable entry | P1 | **S** | Smoke miss |
| 18 | A02-004 | Ensure emoji key default visible / document XOR | P0 | **S** | AVD missing emoji key |
| 19 | A13 polish | Richer clipboard UI / categories | P1 | **M** | MVP OK |
| 20 | Docs false-negatives | Matrix rows for comma→Settings, emoji suggestions, hold-for-symbols | Research | **M** | See below |

---

## Research gaps (Agent 1)

### Critical

1. **Stale baseline docs after Agent 2** — `gboard-settings-inventory.md` and `gboard-surfaces-map.md` still mark clipboard / one-hand / password number-row / incognito as Missing. **Area coverage exists in matrix, but inventory/surfaces are false-negative for shipped work** → CRITICAL research hygiene gap for next cycle.  
2. **Incomplete Gboard Preferences dump** — Agent 1 noted settings activity not launchable; inventory still hybrid. Risk of missing preference labels.

### False-negative missing features (docs/inventory vs matrix)

| Feature (from Help / secondary) | In matrix? | Gap |
|---------------------------------|------------|-----|
| Long-press comma → Settings | **No dedicated row** | Missed atomic feature |
| Emoji suggestions in strip | Inventory only | Not an acceptance row |
| Emoji fast-access / recent letter-board row | Inventory | Not matrixed |
| Hold letter for **symbols** (vs accents) | Blurred into A06 / digit hints | Needs clarify row |
| Add-to-dictionary from suggestion | Inventory / A15-005 | Weak linkage |
| Multilingual auto-detect typing | Not covered | Optional OF — absent |

### Area holes

**None** — areas 1–21 present. No CRITICAL empty area.

### In-scope / OOS vs constitution

| Check | Result |
|-------|--------|
| No swipe-typing | Aligned (Principle V + OOS) |
| Offline suggestions allowed | Aligned (Product Constraints) |
| Personal learning later / on-device | Aligned; LearningPolicy foreshadows |
| EN/RU only | Aligned |
| No Google brand/GMS | Aligned |
| Phone + landscape, no extract | Aligned |
| Floating / voice / translate / GIF / cloud | Correctly hard-OOS |

**Minor tension**: system-wide clipboard capture (local) is privacy-sensitive; OOS allows local clipboard — OK if disclosed (RU settings summary present).

### Non-atomic matrix rows

| Rows | Issue |
|------|-------|
| A07-007 ↔ A21-005 | Duplicate tracker |
| A11-003 ↔ A14-010 | Duplicate resize |
| A15-003 | Mega “preferences subset” |
| A09-003 | Multi IME actions in one row |
| A02-004 | Entire bottom chrome compound |
| A11-001 | Features grid packs many actions |

---

## Implementation defects (Agent 2)

### Blockers (report only — no code fix this pass)

| Severity | Defect | IDs |
|----------|--------|-----|
| **P0 Incorrect** | Suggestion/toolbar strip still composed for suppress editors | A10-006, A16-001 (+ Partial A16-002/003) |
| **P0 Partial** | Space swipe not live-proven; caret uses UTF-16 offsets | A07-007, A21-005 |
| **P0 Partial** | LearningPolicy unused on typing path | A19-002 |

### Quality / MVP overclaim

| Defect | IDs |
|--------|-----|
| One-hand is rail-only, not shrink | A11-002 |
| Reject is LP-only, not drag-trash | A10-007 |
| Features menu thin; smoke failed to open | A11-001 |
| Emulator missing emoji key (pref or layout) | A12-001, A02-004 |
| Did not refresh Agent 1 inventory/surfaces after shipping | Process |

### Positive findings

- Space swipe helpers + KeyGrid gesture wiring are real.  
- RU variants + `прив`→`привет` unit-tested.  
- Number pad, password number-row pref, clipboard TTL/pin, reject store, FeaturesMenu (no mic/search), theme cycle — code exists.  
- No hard OOS features in FeaturesMenu / settings.  
- Dark charcoal + Shift CAPS paths intact.

---

## Emulator screening (Part C)

**Emulator: YES** (`emulator-5554`).  

Notes: `evidence/agent3-emulator-smoke.md` + PNGs `agent3-01`…`agent3-10`.  
Partial automation; Compose key hit-testing unreliable via raw coords. Password strip and space swipe **not** live-proven.

---

## Recommended next Multitask cycle backlog

### Cycle A — P0 correctness (must)

1. **Fix strip suppress** — hide SuggestionBar/Features chrome when `suppressSuggestions` (keep optional nav for non-suppress).  
2. **Wire LearningPolicy** on any future/current learn writes; add controller hook even if NoOp.  
3. **Instrumented / manual QA pack** for space swipe, password (no strip), emoji open, double-space, Caps.  
4. Refresh **settings inventory + surfaces map** statuses to match code.

### Cycle B — P1 comfort (should)

1. Resize design spike (metrics/inset plan) then A11-003/A14-010.  
2. One-hand true shrink + persist.  
3. Suggestion reject drag-trash polish.  
4. Sound volume / haptic strength sliders.  
5. Features menu reliability + emoji when key hidden.

### Cycle C — Research hygiene

1. Deduplicate matrix (A07-007/A21-005, A11-003/A14-010).  
2. Add atomic rows: comma→Settings, emoji strip suggestions, hold-for-symbols clarify.  
3. Re-dump Gboard 18.5.6 Preferences on a device where launcher works.

### Explicitly defer / do not implement

Autocorrect (A10-009), undo BS (A21-004), emoji/lang XOR product (A07-008), screenshots clipboard (A13-004), all hard OOS.

---

## Artifact index

| Path | Role |
|------|------|
| `specs/002-gboard-parity/agent3-audit-report.md` | This report |
| `specs/002-gboard-parity/agent3-matrix-rescored.md` | Full In-scope rescore + evidence |
| `specs/002-gboard-parity/evidence/agent3-emulator-smoke.md` | Smoke notes |
| `specs/002-gboard-parity/evidence/agent3-*.png` | Screenshots |

## Orchestrator return card

| Field | Value |
|-------|-------|
| **Verdict** | `PASS_WITH_GAPS` |
| **P0 coverage** | Done **90.7%** (68/75); Done+Partial **97.3%** |
| **P1 coverage** | Done **68.3%** (28/41); Done+Partial **90.2%** |
| **Top 5 gaps** | (1) strip suppress Incorrect A10-006 (2) resize Missing A11-003 (3) space swipe Partial A07-007 (4) one-hand Partial A11-002 (5) reject UX Partial A10-007 |
| **Report** | `specs/002-gboard-parity/agent3-audit-report.md` |
| **Rescored matrix** | `specs/002-gboard-parity/agent3-matrix-rescored.md` |
| **Emulator** | **Yes** (partial smoke) |
