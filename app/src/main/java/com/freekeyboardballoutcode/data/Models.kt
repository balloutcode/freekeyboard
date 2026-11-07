package com.freekeyboardballoutcode.data

enum class InputLanguage(val tag: String, val displayNameRu: String) {
    EN("en-US", "Английский"),
    RU("ru-RU", "Русский"),
}

enum class KeyboardLayer {
    LETTERS,
    SYMBOLS,
    EMOJI,
    NUMBER,
}

enum class OneHandedSide {
    LEFT,
    RIGHT,
}

enum class ThemePreference {
    LIGHT,
    DARK,
    OLED_DARK,
}

enum class ShiftMode {
    OFF,
    ONCE,
    CAPS,
}

/**
 * User settings mirroring Gboard Preferences / Theme / Text correction,
 * excluding Google services, glide typing, translate, voice, floating keyboard.
 */
data class UserPreferences(
    val theme: ThemePreference = ThemePreference.LIGHT,
    val keyBorders: Boolean = true,
    val enabledLanguages: Set<InputLanguage> = setOf(InputLanguage.EN, InputLanguage.RU),
    val lastActiveLanguage: InputLanguage = InputLanguage.EN,
    val recentEmoji: List<String> = emptyList(),
    // Preferences → Keys / Key press
    val numberRow: Boolean = false,
    val showEmojiKey: Boolean = true,
    val soundOnKeypress: Boolean = false,
    val hapticOnKeypress: Boolean = true,
    val popupOnKeypress: Boolean = true,
    val longPressForSymbols: Boolean = true,
    val longPressDelayMs: Int = 300,
    // Text correction
    val showSuggestionStrip: Boolean = true,
    val nextWordSuggestions: Boolean = true,
    val blockOffensiveWords: Boolean = true,
    val autoCapitalization: Boolean = true,
    val doubleSpacePeriod: Boolean = true,
    /** Show dedicated number row in password fields even when global number row is off. */
    val numberRowForPasswords: Boolean = false,
    val oneHandedMode: Boolean = false,
    val oneHandedSide: OneHandedSide = OneHandedSide.RIGHT,
    /** Opt-in: clipboard history is off until the user enables it in Settings. */
    val clipboardHistoryEnabled: Boolean = false,
    val soundVolume: Float = 1f,
    val hapticStrength: Float = 1f,
    val emojiFontScale: Float = 1f,
    /** Offline autocorrect on space (dictionary edit-distance). */
    val autocorrectEnabled: Boolean = false,
    val personalDictionary: List<String> = emptyList(),
) {
    fun withLanguageEnabled(language: InputLanguage, enabled: Boolean): UserPreferences {
        val next = if (enabled) {
            enabledLanguages + language
        } else {
            enabledLanguages - language
        }
        if (next.isEmpty()) return this
        val last = if (lastActiveLanguage in next) lastActiveLanguage else next.first()
        return copy(enabledLanguages = next, lastActiveLanguage = last)
    }
}
