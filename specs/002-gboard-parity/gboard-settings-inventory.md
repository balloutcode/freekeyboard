# Gboard settings inventory → freekeyboard map

**Gboard observed version (emulator)**: 18.5.6.993608717-release-x86_64  
**Inventory confidence**: Hybrid — official Help + secondary guides + package presence. Full UI walk incomplete (`evidence/emulator-session.md`). Gaps called out.

## Access paths (Gboard)

1. Toolbar / features menu → Settings / More Settings  
2. Long-press comma → drag to Settings (Help)  
3. System Settings → System → Languages & input → On-screen keyboard → Gboard  
4. Gboard app launcher (may be hidden via “Show app icon”; not startable via `am start` on this AVD)

## Root settings (typical tree)

| Screen / item | Type | Offline? | freekeyboard | Gap note |
|---------------|------|----------|--------------|----------|
| Languages | Nav | yes | Languages EN/RU toggles | No multi-layout per language (QWERTZ etc.) |
| Preferences | Nav | yes | Flattened “Preferences” section | Structure differs |
| Theme | Nav | yes | Light/Dark/OLED radios | No photo/dynamic themes |
| Text correction | Nav | yes | “Text correction” section | Subset only |
| Dictionary | Nav | yes | — | Missing personal dict |
| Clipboard | Nav | yes* | — | Missing local clipboard |
| Glide typing | Nav | yes | — | **OOS** |
| Voice typing | Nav | no/GMS | — | **OOS** |
| Emojis, stickers & GIFs | Nav | mixed | Emoji key toggle only | GIF/stickers **OOS** |
| Privacy / Advanced | Nav | mixed | About only | No personalization / delete learned |
| Spell checker / system | Nav | yes | — | Usually system integration |
| About / Help / Version | Info | yes | About body | No Google Help links |

\*Clipboard is local until sync/screenshots cloud features.

---

## Preferences (detailed)

### Keys / layout

| Toggle / control | Gboard behavior | fk mapping | Status |
|------------------|-----------------|------------|--------|
| Number row | Always show 1–0 above letters | `number_row` | Done |
| Number row for passwords | Show number row in password fields | — | Missing |
| Show emoji switch key | Bottom-row emoji key | `show_emoji_key` | Done |
| Show language switch key | Globe key; XOR tip with emoji | Globe when ≥2 langs | Partial (no XOR) |
| Show suggestion strip | Strip visibility | `show_suggestion_strip` | Done |
| One-handed mode | Shrink + dock | — | Missing |
| Keyboard height / Resize | Toolbar Resize (height presets removed) | Fixed `GboardMetrics` | Partial |
| Key borders / theme borders | Theme-dependent | `key_borders` | Done |
| Show app icon | Launcher icon | N/A | OOS/N/A |

### Key press

| Toggle / control | Gboard | fk | Status |
|------------------|--------|----|--------|
| Sound on keypress | On/off + volume | `sound_on_keypress` | Partial (no volume) |
| Keyboard vibration | On/off + strength | `haptic_on_keypress` | Partial (no strength) |
| Popup on keypress | Preview bubble | `popup_on_keypress` | Done |
| Touch & hold delay | Duration slider | `long_press_delay_ms` 100–700 | Done |
| Touch & hold keys for symbols | Symbol hints + hold inserts | `long_press_for_symbols` | Partial (variants, not full Gb symbol map) |

### Other Preferences often present

| Item | Notes | fk |
|------|-------|----|
| Auto-capitalization | Also under Text correction | Done |
| Double-space period | Pref / text correction family | Done |
| Comma / period keys | Dedicated bottom keys | Present on layouts |
| Vibration strength of keypress | ms slider | Missing |
| Sound volume | Slider | Missing |

---

## Text correction

| Item | Gboard | fk | Status |
|------|--------|----|--------|
| Show suggestion strip | Toggle | Done | |
| Next-word suggestions | Toggle | Done | |
| Block offensive words | Toggle | Done | |
| Auto-correction | Replace as you type | — | Missing (product decision) |
| Auto-capitalization | Sentence start | Done | |
| Double-space period | `. ` | Done | |
| Suggest contacts | Often GMS | — | OOS/avoid |
| Emoji suggestions | In strip | — | Missing |
| Smart Compose | Cloud/US | — | OOS |
| Undo autocorrect via BS | After correction | — | Blocked |

---

## Dictionary

| Item | fk |
|------|----|
| Personal dictionary | Missing |
| Delete learned words | Missing (no learn store yet) |
| Add from underlined word | Missing |

---

## Clipboard

| Item | fk | Scope |
|------|----|-------|
| Enable clipboard history | Missing | OF / P1 decision |
| Pin items | Missing | OF |
| Auto-clear ~1 hour | Missing | OF |
| Save recent screenshots | Missing | Privacy-sensitive; decide |
| Smart chips (address/phone) | Missing | Decide / low |

---

## Theme

| Item | fk |
|------|----|
| Built-in Light/Dark | Light/Dark/OLED Done |
| Color / dynamic / wallpaper | Missing (brand risk) |
| Key borders | Done |

---

## Emojis, stickers & GIFs

| Item | fk | Scope |
|------|----|-------|
| Emoji size | Missing | P2 OF |
| Recent/popular on letter board | Missing | P2 |
| Emoji fast-access row | Missing | P2 |
| GIF / Stickers / Bitmoji | — | OOS |
| Emoji search | Missing | P2 OF |

---

## Privacy / Advanced (Gboard)

| Item | Scope |
|------|-------|
| Personalize for you | OOS |
| Improve for everyone / federated learning | OOS |
| Delete learned words & data | OF once learning exists |
| Audio donations | OOS |
| Incognito indicator / respect no-learn flag | OF — Missing in fk |

---

## freekeyboard settings UI (as-built flat tree)

Russian UI (`SettingsScreens.kt`):

1. Setup — enable IME / select IME  
2. Languages — EN / RU  
3. Theme — Light / Dark / OLED + key borders  
4. Preferences — number row, emoji key, sound, haptic, popup, long-press symbols, delay slider  
5. Text correction — suggestions, next-word, offensive, auto-cap, double-space  
6. About  

**Structural gap**: No Clipboard / One-handed / Dictionary / Privacy / Emoji size / Resize screens.

## Explicit inventory gaps

- Exact string labels & ordering for Gboard 18.5.6 PreferenceFragment not dumped (settings activity not launched on AVD).  
- OEM skins may hide/show “Number row for passwords”, “Show app icon”, Proofread.  
- Agent 2 should re-verify Preferences on a device with Gboard launcher when implementing P1 settings parity.
