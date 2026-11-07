# Gboard surfaces map (parity-oriented)

Maps Gboard UI surfaces a user can reach while typing. Marks offline-feasible vs OOS.

## Legend

- **OF** = offline-feasible for freekeyboard  
- **OOS** = out of scope (see `out-of-scope.md`)  
- **fk** = freekeyboard status

## Surface tree

```
IME window (bottom dock; landscape not fullscreen extract)
├── [Toolbar / suggestion strip]
│   ├── Suggestion chips (≤3 words)                         OF  fk=Partial
│   ├── Features menu (grid / “Search and more”)            mixed
│   │   ├── Theme                                           OF  fk=Partial (settings only)
│   │   ├── Clipboard                                       OF  fk=Missing
│   │   ├── One-handed                                      OF  fk=Missing
│   │   ├── Resize                                          OF  fk=Missing
│   │   ├── Emoji                                           OF  fk=Partial
│   │   ├── GIF / Stickers / Bitmoji                        OOS
│   │   ├── Voice                                           OOS
│   │   ├── Translate                                       OOS
│   │   ├── Floating keyboard                               OOS
│   │   └── Settings gear → Settings Activity               OF  fk=Partial
│   └── Mic / Google Search button                          OOS
├── Letter layer (EN QWERTY / RU ЙЦУКЕН / …)
│   ├── Optional number row 1–0                             OF  fk=Done
│   ├── Letter rows + digit/symbol hints                    OF  fk=Done
│   ├── Shift / Caps                                        OF  fk=Done
│   ├── Backspace                                           OF  fk=Done
│   ├── ?123 → Symbols                                      OF  fk=Done
│   ├── Emoji key (optional)                                OF  fk=Done
│   ├── Globe / language key (optional, XOR emoji in Gb)    OF  fk=Done (both allowed)
│   ├── Space (label=language; LP=lang; swipe=cursor)       OF  fk=Partial
│   ├── Comma / Period                                      OF  fk=Done
│   └── Enter / IME action                                  OF  fk=Done
├── Symbols layer (?123)
│   ├── Digit row + punctuation                             OF  fk=Done
│   ├── Shift / =\< → symbols alt                           OF  fk=Done
│   ├── Long-press expanded symbols                         OF  fk=Done
│   └── ABC → Letters                                       OF  fk=Done
├── Emoji panel
│   ├── Category tabs + recents                             OF  fk=Done
│   ├── Search                                              OF optional / P2
│   ├── GIF / Stickers                                      OOS
│   └── ABC return                                          OF  fk=Done
├── Clipboard panel                                         OF  fk=Missing
├── Language picker / switch UI                             OF  fk=Done
├── One-handed chrome (side rail, expand, flip side)        OF  fk=Missing
├── Resize handles                                          OF  fk=Missing
└── Floating window                                         OOS
```

## Interaction overlay surfaces

| Overlay | Trigger | OF | fk |
|---------|---------|----|----|
| Key press popup | Tap letter (pref) | yes | Done |
| Variant popup | Long-press key | yes | Done |
| Language picker | Long-press Space / globe | yes | Done |
| Suggestion reject (trash) | Long-press suggestion | yes | Missing |
| Caps lock latch visual | Double-tap / hold Shift | yes | Done |

## Editor-type surfaces (inputType)

| Editor class | Gboard typical | freekeyboard |
|--------------|----------------|--------------|
| Text (normal) | Letters + suggestions | Done |
| Password | Often number row / no suggestions / no learn | Partial (suppress suggestions; no special layout) |
| Email / URI | Letters; often no suggestions | Suppress suggestions Done |
| Phone / Number | Digit pad | Missing dedicated pad |
| TYPE_NULL | Raw key events | Partial/Done Latin mapping |
| Incognito / no personalized learning | No learn; often no cloud | Missing flag handling |

## Orientation

| Mode | Gboard | freekeyboard |
|------|--------|--------------|
| Portrait bottom IME | Default | Done |
| Landscape bottom IME | Usually docked; extract optional | Fullscreen extract forced off — Done |
| Split landscape | Tablets | OOS (phones) |
