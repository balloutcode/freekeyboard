# Contract: System IME Registration

**As-built**: 2026-11-08

## Application

- `applicationId` / namespace: `com.freekeyboardballoutcode`
- `android:allowBackup="false"`
- `android:fullBackupContent="@xml/backup_rules"` — excludes DataStore
- `android:dataExtractionRules="@xml/data_extraction_rules"` — excludes DataStore from cloud backup / device transfer
- No `INTERNET` permission

## Manifest service

- `android:permission="android.permission.BIND_INPUT_METHOD"`
- Intent action: `android.view.InputMethod`
- Meta-data `android.view.im` → `@xml/method`
- Exported service as required for IME binding on target SDK

## method.xml

- `android:settingsActivity` → settings Activity FQCN
- `android:supportsInlineSuggestions="true"` (feature 003)
- `android:supportsSwitchingToNextInputMethod="true"`
- Subtype EN: `android:languageTag="en-US"`, `android:imeSubtypeMode="keyboard"`
- Subtype RU: `android:languageTag="ru-RU"`, `android:imeSubtypeMode="keyboard"`
- Labels via resources; product name freekeyboard only

## Settings Activity

- Launchable from IME settings affordance and launcher
- Russian UI strings
- Reads/writes shared UserPreferences; disabling clipboard history clears `ClipboardHistoryStore`

## Release

- R8 minify + shrinkResources on `release` build type
- Keep rules for `FreekeyboardImeService`, `SettingsActivity`, androidx.autofill
