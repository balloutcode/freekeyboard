# Agent 1 research notes — checklist

**Agent**: 1 Deep Gboard Research  
**Date**: 2026-11-07  
**Production code changes**: none  

## Checklist

- [x] Read `specs/001-ime-core-en-ru/*` (spec, research, data-model, contracts, tasks)  
- [x] Read key source under `app/src/main/java/com/freekeyboardballoutcode/**`  
- [x] Write `freekeyboard-baseline.md`  
- [x] Research public Gboard Help + secondary settings guides  
- [x] Write `gboard-docs-digest.md` with URLs  
- [x] Emulator: locate adb, device `emulator-5554`, Gboard 18.5.6 present  
- [x] Emulator: switch to Gboard briefly; pull APK; screenshots/UI dumps → `evidence/`  
- [x] Document emulator gaps (settings launcher not startable; partial interaction)  
- [x] Write `gboard-surfaces-map.md`  
- [x] Write `gboard-settings-inventory.md` (gaps explicit)  
- [x] Write `gboard-feature-matrix.md` (≥100 atomic rows, areas 1–21)  
- [x] Write `out-of-scope.md`  
- [x] Write `README.md` for Agent 2  
- [x] Restore freekeyboard as default IME after observation  
- [x] No git commit/push  

## Emulator session facts

| Item | Value |
|------|-------|
| Device | emulator-5554 sdk_gphone64_x86_64 |
| Display | 720×1280 @ 320 dpi |
| Android | 15 |
| Gboard | 18.5.6.993608717-release-x86_64 |
| Full interactive area pass | No — settings activity not launchable; Chrome/Messaging focus partial |
| Evidence | `evidence/emulator-session.md` + png/xml/txt |

## Method notes

1. Behavioral claims for Shift/Caps/Space-LP/suggestions primarily from Google Help 2842292 / 7068415 + freekeyboard 001 as-built (already aiming at those behaviors).  
2. Settings tree filled from Help + Pocket-lint / MakeTechEasier / Android Authority / 9to5Google because official Help lacks a full Preferences dump.  
3. APK aapt/ascii string mining saved under `evidence/` for Agent 2 cross-check (noisy; not authoritative labels).  

## Matrix quality self-check

| Criterion | OK? |
|-----------|-----|
| Areas 1–21 present | yes |
| ≥100 atomic rows | yes (139) |
| Acceptance test per row | yes |
| In-scope + Priority filled | yes |
| OOS called out | yes |

## Recommended Agent 2 first slice

1. `A07-007` spacebar cursor swipe (P0 Missing)  
2. `A10-010` / dict quality (P0 Partial)  
3. `A06-006` RU variants audit (P0 Partial)  
4. Then P1: `A16-006` number pad, `A11` offline toolbar subset per product decisions  
