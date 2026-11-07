# freekeyboard — release minify / shrink

# IME + launcher entry points (manifest-referenced).
-keep class com.freekeyboardballoutcode.ime.FreekeyboardImeService { *; }
-keep class com.freekeyboardballoutcode.settings.SettingsActivity { *; }

# Inline autofill UI inflation (platform + androidx.autofill).
-keep class androidx.autofill.** { *; }
-dontwarn androidx.autofill.**

# Compose / coroutines commonly needed with R8 full mode.
-dontwarn kotlinx.coroutines.**
-keepclassmembers class * extends androidx.datastore.preferences.protobuf.GeneratedMessageLite {
    <fields>;
}
