# Agent 3 — emulator smoke notes

**Date**: 2026-11-07  
**Device**: emulator-5554 (`sdk_gphone64_x86_64`, 720×1280 @ 320dpi)  
**IME**: `com.freekeyboardballoutcode/.ime.FreekeyboardImeService` (default; `mInputShown=true`)  
**Host field**: Messages “New chat” → ContactSearchField (`To:`)

## Captures (binary PNG via `adb pull`)

| File | Intent | Result |
|------|--------|--------|
| `agent3-01-keyboard-shown.png` | IME visible, dark charcoal | **PASS** — EN QWERTY, space `Английский`, digit hints, strip area present |
| `agent3-02-hello-typed.png` | Type Hello | **PASS*** — text via `adb input text` (host inject; not key-face taps) |
| `agent3-05-symbols.png` | ?123 layer | **PASS** — digit/punct page; ABC on bottom-left |
| `agent3-06-caps.png` | Double-tap Shift | **PARTIAL** — Shift highlight observed; full CAPS lock not proven by typing |
| `agent3-07-emoji.png` | Emoji panel | **MISS** — landed on RU letters (globe hit); emoji key not clearly present in bottom row |
| `agent3-08-abc-return.png` | Return letters | RU layout still showing |
| `agent3-09-features.png` | Features grid | **MISS** — tap coords did not open FeaturesMenu |
| `agent3-10-landscape.png` | Landscape dock | **PASS** — bottom soft KB, RU ЙЦУКЕН, no fullscreen extract |

\*Host `input text` bypasses key faces; proves editor connection + IME attached, not key hit-testing.

## Scenario checklist (requested)

| Scenario | Result | Notes |
|----------|--------|-------|
| Hello | Partial | Host inject OK; key-face Hello not proven |
| Globe → RU | Pass incidental | Tap intended for emoji switched to `Русский` |
| Symbols | Pass | Digits/`@#$…` visible |
| Caps | Partial | Shift lit after double-tap attempt |
| Emoji | Fail (coords/key) | No emoji panel; emoji key may be pref-off on AVD |
| ABC | N/A after miss | |
| Suggestion | Partial | `Hello` chip seen in 01; strip often empty later |
| Double-space | Not run | Static/code only |
| Password (no strip) | Not run live | **Static defect**: strip chrome not gated by suppress (see audit) |
| Landscape | Pass | |

## P0 acceptance spot-checks (sample)

| ID | Live? | Notes |
|----|-------|-------|
| A02-001 EN QWERTY | Yes | Visible |
| A02-003 space label | Yes | `Английский` / `Русский` |
| A03-001/002 symbols | Yes | |
| A14-002 dark charcoal | Yes | Neutral greys |
| A17-002 landscape | Yes | Docked |
| A07-007 space swipe | No | Code+unit only |
| A10-006 password strip | No | Static Incorrect |

## Disclaimer

Coordinate taps on Compose IME keys are approximate; several UI targets missed. Treat live results as **supporting**, not exhaustive acceptance. Full P0 suite still needs a scripted Compose UI / accessibility pass.
