# freekeyboard

Свободная системная клавиатура для Android (цель: F-Droid) с UX, близким к актуальному Gboard, без бренда Google.

- **applicationId**: `com.freekeyboardballoutcode`
- **Лицензия**: GPL-3.0 (см. [LICENSE](LICENSE))
- **UI настроек**: русский
- **Ввод**: английский (QWERTY) + русский (ЙЦУКЕН)
- **Spec Kit**: артефакты в `specs/` + конституция `.specify/memory/constitution.md` (**v1.1.0**)
- **Cursor skills**: `.cursor/skills/` (speckit-*, emulator-nojunkie)

## Статус (2026-11-08)

| Фича | Статус |
|------|--------|
| [001-ime-core-en-ru](specs/001-ime-core-en-ru/) | Реализовано: IME, EN/RU, Caps, emoji, темы, offline suggestions |
| [002-gboard-parity](specs/002-gboard-parity/) | Реализовано (Agent 4 PASS); resize/emoji polish — deferred |
| [003-inline-autofill](specs/003-inline-autofill/) | Реализовано; чипы зависят от Autofill-провайдера с датасетами |
| [004-privacy-hardening](specs/004-privacy-hardening/) | Реализовано: backup/clipboard/password guards + Bugbot fixes |

## Стек

Kotlin 2.4.21 · Compose BOM 2026.09.00 · AGP 9.5.1 · Gradle 9.8.2 · JDK 21 · minSdk 31 · compile/targetSdk 37

AGP 9 использует встроенный Kotlin — плагин `org.jetbrains.kotlin.android` не применяется; для Compose остаётся `org.jetbrains.kotlin.plugin.compose`.

Release: R8 minify + shrinkResources.

## Сборка

Требуется Android SDK (API 37) и JDK 21+ (подойдёт JBR из Android Studio).

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:assembleRelease
.\gradlew.bat :app:testDebugUnitTest
```

APK: `app/build/outputs/apk/debug/app-debug.apk` · release: `app/build/outputs/apk/release/app-release.apk`

## Установка и включение IME

1. Установите APK на устройство/эмулятор API 31+.
2. Откройте **freekeyboard** → «Включить клавиатуру в системе».
3. Включите **freekeyboard**, затем выберите её активной IME.
4. Откройте текстовое поле и наберите EN/RU.

Эмулятор на этой машине: skill [emulator-nojunkie](.cursor/skills/emulator-nojunkie/SKILL.md).

## Возможности

- EN QWERTY / RU ЙЦУКЕН, 🌐 и долгий Space
- Символы, Caps (double-tap/hold), long-press варианты, emoji
- Offline suggestions + опциональный autocorrect; личный словарь on-device
- Темы: Светлая / Тёмная (charcoal) / OLED
- Свайп по пробелу → курсор; **без** glide typing / voice / GIF / translate / floating
- Number/phone pad; one-hand; Features-меню
- История буфера: **выкл. по умолчанию**, локально, clear при выключении
- Inline autofill чипы менеджеров паролей (Android 11+)
- `allowBackup=false`; password-safe space; Features toolbar на password-полях

## Spec Kit

```text
specs/
  001-ime-core-en-ru/     # core IME SDD + contracts + quickstart
  002-gboard-parity/      # parity SDD + research/QA archive
  003-inline-autofill/    # inline autofill
  004-privacy-hardening/  # security + Bugbot hardening
.specify/memory/constitution.md
```
