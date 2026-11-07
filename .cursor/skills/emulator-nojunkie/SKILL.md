---
name: emulator-nojunkie
description: >-
  Launch the Android emulator on this machine the way Android Studio External
  Tool "Эмулятор NoJunkie" does. Use whenever freekeyboard needs an emulator,
  adb device, smoke test, Gboard/live IME QA, or APK install — never use a bare
  `emulator -avd …` without these flags on this system.
---

# Emulator NoJunkie (this machine)

## Rule

On Alex's Windows system the emulator **does not** start reliably with a plain SDK `emulator` launch. Always use the **NoJunkie** recipe below (same as Android Studio → Tools → External Tools → **Эмулятор NoJunkie**).

Do **not** use only:

```bat
emulator -avd Medium_Phone
```

## Exact command (from Android Studio External Tools.xml)

Config source (do not edit unless user asks):

`%APPDATA%\Google\AndroidStudio*\tools\External Tools.xml`  
Tool name: `Эмулятор NoJunkie`

Decoded launch:

```bat
cmd.exe /c "set QT_OPENGL=software&& set ANDROID_EMU_ENABLE_CRASH_REPORTING=0&& \"%LOCALAPPDATA%\Android\Sdk\emulator\emulator.exe\" -avd Medium_Phone -gpu swiftshader_indirect -feature -Vulkan -feature -GLDirectMem -cores 3 -memory 3072"
```

Notes:

- AVD id: **`Medium_Phone`**
- GPU: `swiftshader_indirect` (software GL)
- Features disabled: `Vulkan`, `GLDirectMem`
- Env: `QT_OPENGL=software`, `ANDROID_EMU_ENABLE_CRASH_REPORTING=0`
- Studio uses `cmd /k` (keeps console open); agents should prefer **`cmd /c`** in background, or run `emulator.exe` directly with the same env/args and `block_until_ms: 0`

## Recommended agent launch (PowerShell)

```powershell
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
$env:Path = "$env:ANDROID_HOME\platform-tools;$env:ANDROID_HOME\emulator;$env:Path"
$env:QT_OPENGL = "software"
$env:ANDROID_EMU_ENABLE_CRASH_REPORTING = "0"
# Start detached / background — do not block the agent forever
Start-Process -FilePath "$env:ANDROID_HOME\emulator\emulator.exe" -ArgumentList @(
  "-avd", "Medium_Phone",
  "-gpu", "swiftshader_indirect",
  "-feature", "-Vulkan",
  "-feature", "-GLDirectMem",
  "-cores", "3",
  "-memory", "3072"
)
```

Then poll until online:

```powershell
adb wait-for-device
# optionally loop until `adb devices` shows emulator-5554 as "device" (not offline)
```

## After boot

- Prefer serial `emulator-5554` when present
- Install: `adb install -r app\build\outputs\apk\debug\app-debug.apk`
- Default IME for freekeyboard work: `com.freekeyboardballoutcode/.ime.FreekeyboardImeService`

## When this skill applies

Any freekeyboard task that needs a running emulator, live Gboard comparison, IME smoke, or APK sideload on this developer machine.
