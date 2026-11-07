# 002 — Gboard parity

**Status (2026-11-08)**: **Implemented / Agent 4 PASS**. In-scope P0–P1 closed except deferred resize & emoji polish. Privacy follow-ups: [004](../004-privacy-hardening/). Inline autofill: [003](../003-inline-autofill/).  
**Constraint**: FOSS/GPL/F-Droid; no GMS/Google brand.

## Living SDD

| File | Role |
|------|------|
| [spec.md](./spec.md) | WHAT — current acceptance |
| [plan.md](./plan.md) | HOW — architecture map |
| [tasks.md](./tasks.md) | Done vs deferred checklist |

## Research / QA archive (historical)

| File | Role |
|------|------|
| [gboard-feature-matrix.md](./gboard-feature-matrix.md) | Atomic rows (may lag code; prefer Agent 3 rescored + tasks) |
| [agent3-matrix-rescored.md](./agent3-matrix-rescored.md) | Authoritative mid-pipeline rescore |
| [agent4-bugs.md](./agent4-bugs.md) / [evidence/agent4-qa-report.md](./evidence/agent4-qa-report.md) | Final emulator QA PASS |
| [out-of-scope.md](./out-of-scope.md) | Hard exclusions (glide = swipe-typing OOS; caret swipe in-scope) |
| [evidence/autofill-emulator-smoke.md](./evidence/autofill-emulator-smoke.md) | Inline autofill AVD smoke (chips blocked without datasets) |

## Product clarifications

- «Свайпы» OOS = **glide typing** only; spacebar caret swipe stays in-scope.
- Keyboard **resize** intentionally deferred.
- Clipboard history is **opt-in** (default off) after 004.
