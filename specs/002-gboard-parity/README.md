# 002 — Gboard parity research (Agent 1 handoff)

**Status**: Agent S screen **PASS_WITH_GAPS** (P0 100% / P1 97.4%). Agent T emulator smoke **FAIL** 11/2 (LP Space picker + one-hand layout). Glide OOS; caret swipe Done. See `agentS-screen-report.md`, `evidence/agentT-emulator-smoke.md`.  
**Date**: 2026-11-07  
**Constraint**: FOSS/GPL/F-Droid; no GMS/Google brand. See `agent3-audit-report.md` / `agent3-matrix-rescored.md` (authoritative rescore); Agent 2 log/backlog remain historical.

## Purpose

Atomic behavioral/UX parity backlog for Agent 2. Parity means **covered controls behave like contemporary Gboard**, not a pixel clone and not a copy of out-of-scope Google features.

## Artifact index (Agent 2 start here)

| File | Role |
|------|------|
| [gboard-feature-matrix.md](./gboard-feature-matrix.md) | **Primary TZ** — atomic rows, acceptance tests, P0/P1/P2, Done/Partial/Missing |
| [freekeyboard-baseline.md](./freekeyboard-baseline.md) | As-built freekeyboard vs 21 Gboard areas |
| [gboard-surfaces-map.md](./gboard-surfaces-map.md) | Keyboard surfaces / layers / chrome map |
| [gboard-settings-inventory.md](./gboard-settings-inventory.md) | Gboard settings tree + freekeyboard mapping |
| [gboard-docs-digest.md](./gboard-docs-digest.md) | Public Help/docs synthesis + URLs |
| [out-of-scope.md](./out-of-scope.md) | Hard exclusions + product-decision items |
| [agent1-research-notes.md](./agent1-research-notes.md) | Method checklist + progress |
| [agent2-implementation-log.md](./agent2-implementation-log.md) | Agent 2 what shipped + build results |
| [agent2-gap-backlog.md](./agent2-gap-backlog.md) | Deferred / blocked leftovers for Agent 3 |
| [agent3-audit-report.md](./agent3-audit-report.md) | **Agent 3 verdict**, coverage, gaps, backlog |
| [agent3-matrix-rescored.md](./agent3-matrix-rescored.md) | **Authoritative** In-scope Done/Partial/Missing/Incorrect + evidence |
| [spec.md](./spec.md) / [plan.md](./plan.md) / [tasks.md](./tasks.md) | Spec Kit for 002 slice |
| [evidence/](./evidence/) | Emulator notes, screenshots, APK string hits (+ `agent3-emulator-smoke.md`) |

## How Agent 2 should use the matrix

1. Filter `In-scope = yes` and `Priority = P0` first (daily EN+RU typing).
2. Implement/fix only rows with status `Missing` or `Partial`.
3. Treat each row’s **Acceptance test** as the definition of done.
4. Skip / do not implement anything listed in `out-of-scope.md` unless product reopens it.
5. Prefer behavioral match over visual cloning; no Google assets/trademarks.

## Research method summary

- Baseline: `specs/001-ime-core-en-ru/*` + `app/src/main/java/com/freekeyboardballoutcode/**`.
- Docs: support.google.com/gboard (+ secondary Android guides for settings tree gaps).
- Emulator: `emulator-5554`, Android 15, 720×1280@320dpi, Gboard **18.5.6** installed; full interactive walk incomplete (settings launcher not startable; see evidence).

## Quick counts (Agent 3 rescore is authoritative)

- Matrix areas **1–21** covered; **139** rows; **128** In-scope.
- Cycle 2 closed strip/resize/one-hand/reject/feedback/emoji/dict/autocorrect.
- Clarification: «свайпы» OOS = **glide typing**; spacebar caret swipe stays (`A07-007`).
- Emulator: **yes** (Agent 1 research + Agent 3 partial smoke).
