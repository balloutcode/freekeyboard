# Tasks: 004 Privacy & code hardening

**Status**: Complete (2026-11-08)

## Security review

- [x] T001 `allowBackup=false` + backup / data-extraction exclude rules
- [x] T002 Clipboard: `EXTRA_IS_SENSITIVE` filter + keyboard-visible listener only
- [x] T003 Clipboard: default off, clearAll on disable, pin TTL 7d, UUID ids, prunePersisted
- [x] T004 Password: guard `onSpace`; block clipboard panel; skip reject persistence
- [x] T005 Space-swipe avoid large buffers on password/suppress; CURSOR_BUFFER + DPAD
- [x] T006 ClipboardPanel per-entry delete + Features gating
- [x] T007 Release `isMinifyEnabled` + `isShrinkResources` + ProGuard keeps

## Bugbot

- [x] T008 Caret swipe: no absolute `setSelection` on truncated buffer
- [x] T009 Features toolbar when `suppressSuggestions`
- [x] T010 `deleteBeforeCursor` UTF-16 fallback (`utf16UnitsForCodePoints`)
- [x] T011 Multiline Enter → `\n` (`prefersNewlineOnEnter`)
- [x] T012 Word-delete buffer 1024
- [x] T013 Single `onStartInput` (remove duplicate from `onStartInputView`)
- [x] T014 Theme `pendingTheme` + immediate nav chrome
- [x] T015 TalkBack `onLongClick` / delete-word custom action
- [x] T016 Insets first-frame estimate includes strip + post recompute

## Verification

- [x] Unit tests: ClipboardSensitivity, ClipboardHistoryStore prune, EditorInfoInterpreter newline, utf16 helpers, clipboard panel password gate
- [x] `assembleRelease` with R8
