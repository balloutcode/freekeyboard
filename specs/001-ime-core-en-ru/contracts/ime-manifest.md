# Contract: System IME Registration

## Manifest service

- `android:permission="android.permission.BIND_INPUT_METHOD"`
- Intent action: `android.view.InputMethod`
- Meta-data `android.view.im` → `@xml/method`
- Exported service as required for IME binding on target SDK

## method.xml

- `android:settingsActivity` → settings Activity FQCN
- Subtype EN: `android:languageTag="en-US"`, `android:imeSubtypeMode="keyboard"`
- Subtype RU: `android:languageTag="ru-RU"`, `android:imeSubtypeMode="keyboard"`
- Labels via resources; product name freekeyboard only

## Settings Activity

- Launchable from IME settings affordance
- Russian UI strings
- Reads/writes shared UserPreferences

## Application identity

- `applicationId` / namespace: `com.freekeyboardballoutcode`
