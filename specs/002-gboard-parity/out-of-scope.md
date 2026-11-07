# Out of scope & product decisions

Hard exclusions for freekeyboard Gboard **parity** work (FOSS/GPL/F-Droid, offline-first, no GMS/Google brand).

## Hard out of scope (do not implement)

| Item | Reason |
|------|--------|
| Glide / swipe typing (жест-набор по буквам) | Explicit product exclusion; this is what «вырезать свайпы» meant |
| Hold-?123-slide / drag-to-trash chrome | Optional; not required for parity |
| Keyboard resize / height scale | Product decision 2026-11-07: removed drag handle + height prefs |

**Not OOS:** spacebar horizontal swipe to move the caret (`A07-007`) — required Gboard-like behavior.
| Google voice / talk-to-text | Requires Google services; GMS |
| Translate | Network + Google brand/services |
| GIF / sticker / Bitmoji / Emoji Kitchen network tabs | Network packs; Google/Tenor |
| Floating keyboard | Explicit exclusion |
| Cloud personalization / federated learning / audio donations | Privacy + GMS + cloud |
| Google Search / “Search and more” toolbar button | Google brand + network |
| Smart Compose / Proofread “Fix it” cloud chips | Network / Pixel-tied Google features |
| Handwriting layout | Complex ML; often Google-backed; not in EN/RU typing parity brief |
| Material You dynamic wallpaper themes / photo backgrounds | Asset/brand risk; optional later as generic themes only |
| Clipboard sync phone↔PC | Google account |
| Gboard trademarks, logos, proprietary keycap assets | Brand/FOSS |

## Needs product decision (doubtful / optional)

| Item | Why doubtful | Suggested default for Agent 2 |
|------|----------------|-------------------------------|
| Local clipboard history panel (pin, 1h expiry, screenshots) | Offline-feasible; useful; not listed as hard exclusion (001 assumed clipboard OOS) | **P1 candidate** — implement local-only if capacity; else defer |
| One-handed mode (shrink + side dock) | Offline-feasible; Gboard toolbar feature | **P1** comfort; after P0 typing solid |
| Toolbar / features grid (mic-free) | Gboard chrome; freekeyboard has suggestion strip only | Minimal offline toolbar (clipboard/emoji/one-hand) **P1**; no mic/search |
| Spacebar cursor swipe L/R | Clarified in-scope (not glide) | **Done** — see A07-007 |
| Autocorrect / personal learning dictionary | Offline possible; 001 deferred | **P2** unless product elevates |
| Keyboard Resize (drag height/width) | Was comfort candidate | **Hard OOS** (removed 2026-11-07) — see table above |
| Number row for passwords only | Gboard preference; freekeyboard has global number row | **P1** if password UX gaps |
| Emoji size slider / fast-access emoji row | Comfort | **P2** |
| Show language key XOR emoji key (mutual exclusion) | Gboard Preferences quirk; freekeyboard can show both | Match Gboard XOR **or** keep both — **decide**; default keep both (current) |
| Incognito / IME_FLAG_NO_PERSONALIZED_LEARNING | freekeyboard has no learning yet; flag still worth respecting for future | **P1** — suppress any future learn + suggestions extras |
| Accessibility: TalkBack key announcements, large keys | P2 by priority rubric | **P2** |
| Undo of autocorrect via Backspace | Needs autocorrect first | Blocked on autocorrect decision |
| Split keyboard (landscape/tablet) | Phones in-scope; tablets OOS in 001 | **Out** for phones; revisit for tablets |
| Vibration strength / key sound volume sliders | freekeyboard has on/off only | **P2** |
| Key long-press → symbols on letter rows (Gboard “Touch and hold keys for symbols”) | freekeyboard long-press is variants/accents; symbols hints differ | Clarify: keep accent variants **P0**; optional symbol hints **P1** |

## Explicitly not “parity failures”

These Gboard behaviors are **expected absences**, not Missing bugs:

- Mic key, GIF tab, sticker tab, translate chip, Google account sync.
- Cloud suggestion ranking / multilanguage auto-switch via Google.
- Pixel-only Proofread / Smart Compose availability notes in Help.
