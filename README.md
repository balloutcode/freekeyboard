# freekeyboard

Свободная системная клавиатура для Android (цель: F-Droid) с UX, близким к актуальному Gboard, без бренда Google.

- **applicationId**: `com.freekeyboardballoutcode`
- **Лицензия**: GPL-3.0 (см. [LICENSE](LICENSE))
- **UI настроек**: русский
- **Ввод**: английский (QWERTY) + русский (ЙЦУКЕН)
- **Spec Kit**: v1.3.1 (Cursor skills в `.cursor/skills/`)

## Статус

| Фича | Статус |
|------|--------|
| [001-ime-core-en-ru](specs/001-ime-core-en-ru/) | Реализовано: IME, EN/RU, символы, Caps, emoji, темы, offline suggestions |
| 002 — автокоррект / персональный словарь | Запланировано после 001 |

Конституция: [.specify/memory/constitution.md](.specify/memory/constitution.md)

## Стек

Kotlin 2.4.21 · Compose BOM 2026.09.00 · AGP 9.5.1 · Gradle 9.8.2 · JDK 21 · minSdk 31 · compile/targetSdk 37

AGP 9 использует встроенный Kotlin — плагин `org.jetbrains.kotlin.android` не применяется; для Compose остаётся `org.jetbrains.kotlin.plugin.compose`.

## Сборка

Требуется Android SDK (API 37) и JDK 21+ (подойдёт JBR из Android Studio).

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:testDebugUnitTest
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

## Установка и включение IME

1. Установите debug APK на устройство/эмулятор API 31+.
2. Откройте **freekeyboard** → «Включить клавиатуру в системе» (или Настройки → Система → Языки → Экранная клавиатура).
3. Включите **freekeyboard**, затем «Выбрать freekeyboard» (или переключатель клавиатур в панели уведомлений).
4. Откройте любое текстовое поле и наберите EN/RU.

## Возможности (001)

- Ввод EN QWERTY и RU ЙЦУКЕН, переключение 🌐 или долгий Space
- Слой символов/цифр, Shift (раз / Caps Lock double-tap/hold), long-press варианты
- Панель emoji с категориями и «Недавние»
- Offline suggestion strip (без облака); автокоррект / личный словарь — feature 002
- Темы: Светлая / Тёмная (neutral charcoal) / OLED
- Без написания жестами (glide), voice, GIF, translate, floating keyboard
- Свайп влево/вправо по пробелу перемещает курсор (как в Gboard)

## Spec Kit

Артефакты фичи: `specs/001-ime-core-en-ru/` (`spec.md`, `plan.md`, `tasks.md`, contracts, checklists).
