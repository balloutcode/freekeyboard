package com.freekeyboardballoutcode.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferencesRepository(private val context: Context) {
    private val themeKey = stringPreferencesKey("theme")
    private val keyBordersKey = booleanPreferencesKey("key_borders")
    private val enabledLanguagesKey = stringSetPreferencesKey("enabled_languages")
    private val lastLanguageKey = stringPreferencesKey("last_language")
    private val recentEmojiKey = stringPreferencesKey("recent_emoji")
    private val numberRowKey = booleanPreferencesKey("number_row")
    private val showEmojiKeyKey = booleanPreferencesKey("show_emoji_key")
    private val soundOnKeypressKey = booleanPreferencesKey("sound_on_keypress")
    private val hapticOnKeypressKey = booleanPreferencesKey("haptic_on_keypress")
    private val popupOnKeypressKey = booleanPreferencesKey("popup_on_keypress")
    private val longPressForSymbolsKey = booleanPreferencesKey("long_press_for_symbols")
    private val longPressDelayMsKey = intPreferencesKey("long_press_delay_ms")
    private val showSuggestionStripKey = booleanPreferencesKey("show_suggestion_strip")
    private val nextWordSuggestionsKey = booleanPreferencesKey("next_word_suggestions")
    private val blockOffensiveWordsKey = booleanPreferencesKey("block_offensive_words")
    private val autoCapitalizationKey = booleanPreferencesKey("auto_capitalization")
    private val doubleSpacePeriodKey = booleanPreferencesKey("double_space_period")

    val preferences: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        val theme = prefs[themeKey]?.let {
            runCatching { ThemePreference.valueOf(it) }.getOrDefault(ThemePreference.LIGHT)
        } ?: ThemePreference.LIGHT

        val enabled = prefs[enabledLanguagesKey]
            ?.mapNotNull { runCatching { InputLanguage.valueOf(it) }.getOrNull() }
            ?.toSet()
            ?.takeIf { it.isNotEmpty() }
            ?: setOf(InputLanguage.EN, InputLanguage.RU)

        val last = prefs[lastLanguageKey]
            ?.let { runCatching { InputLanguage.valueOf(it) }.getOrNull() }
            ?.takeIf { it in enabled }
            ?: enabled.first()

        val recent = prefs[recentEmojiKey]
            ?.split(RECENT_DELIMITER)
            ?.filter { it.isNotBlank() }
            ?: emptyList()

        UserPreferences(
            theme = theme,
            keyBorders = prefs[keyBordersKey] ?: true,
            enabledLanguages = enabled,
            lastActiveLanguage = last,
            recentEmoji = recent,
            numberRow = prefs[numberRowKey] ?: false,
            showEmojiKey = prefs[showEmojiKeyKey] ?: true,
            soundOnKeypress = prefs[soundOnKeypressKey] ?: false,
            hapticOnKeypress = prefs[hapticOnKeypressKey] ?: true,
            popupOnKeypress = prefs[popupOnKeypressKey] ?: true,
            longPressForSymbols = prefs[longPressForSymbolsKey] ?: true,
            longPressDelayMs = (prefs[longPressDelayMsKey] ?: 300).coerceIn(100, 700),
            showSuggestionStrip = prefs[showSuggestionStripKey] ?: true,
            nextWordSuggestions = prefs[nextWordSuggestionsKey] ?: true,
            blockOffensiveWords = prefs[blockOffensiveWordsKey] ?: true,
            autoCapitalization = prefs[autoCapitalizationKey] ?: true,
            doubleSpacePeriod = prefs[doubleSpacePeriodKey] ?: true,
        )
    }

    suspend fun setTheme(theme: ThemePreference) {
        context.dataStore.edit { it[themeKey] = theme.name }
    }

    suspend fun setKeyBorders(enabled: Boolean) {
        context.dataStore.edit { it[keyBordersKey] = enabled }
    }

    suspend fun setLastActiveLanguage(language: InputLanguage) {
        context.dataStore.edit { prefs ->
            val enabled = prefs[enabledLanguagesKey]
                ?.mapNotNull { runCatching { InputLanguage.valueOf(it) }.getOrNull() }
                ?.toSet()
                ?: setOf(InputLanguage.EN, InputLanguage.RU)
            if (language in enabled) {
                prefs[lastLanguageKey] = language.name
            }
        }
    }

    suspend fun setLanguageEnabled(language: InputLanguage, enabled: Boolean): Boolean {
        var applied = false
        context.dataStore.edit { prefs ->
            val current = prefs[enabledLanguagesKey]
                ?.mapNotNull { runCatching { InputLanguage.valueOf(it) }.getOrNull() }
                ?.toMutableSet()
                ?: mutableSetOf(InputLanguage.EN, InputLanguage.RU)
            if (enabled) {
                current.add(language)
            } else if (current.size > 1) {
                current.remove(language)
            } else {
                return@edit
            }
            prefs[enabledLanguagesKey] = current.map { it.name }.toSet()
            val last = prefs[lastLanguageKey]
                ?.let { runCatching { InputLanguage.valueOf(it) }.getOrNull() }
            if (last == null || last !in current) {
                val preferred = listOf(InputLanguage.EN, InputLanguage.RU)
                    .firstOrNull { it in current }
                    ?: current.minBy { it.name }
                prefs[lastLanguageKey] = preferred.name
            }
            applied = true
        }
        return applied
    }

    suspend fun setBoolean(key: String, value: Boolean) {
        context.dataStore.edit { prefs ->
            when (key) {
                "number_row" -> prefs[numberRowKey] = value
                "show_emoji_key" -> prefs[showEmojiKeyKey] = value
                "sound_on_keypress" -> prefs[soundOnKeypressKey] = value
                "haptic_on_keypress" -> prefs[hapticOnKeypressKey] = value
                "popup_on_keypress" -> prefs[popupOnKeypressKey] = value
                "long_press_for_symbols" -> prefs[longPressForSymbolsKey] = value
                "show_suggestion_strip" -> prefs[showSuggestionStripKey] = value
                "next_word_suggestions" -> prefs[nextWordSuggestionsKey] = value
                "block_offensive_words" -> prefs[blockOffensiveWordsKey] = value
                "auto_capitalization" -> prefs[autoCapitalizationKey] = value
                "double_space_period" -> prefs[doubleSpacePeriodKey] = value
                "key_borders" -> prefs[keyBordersKey] = value
            }
        }
    }

    suspend fun setLongPressDelayMs(ms: Int) {
        context.dataStore.edit { it[longPressDelayMsKey] = ms.coerceIn(100, 700) }
    }

    suspend fun recordEmoji(emoji: String, maxItems: Int = MAX_RECENT_EMOJI) {
        context.dataStore.edit { prefs ->
            val current = prefs[recentEmojiKey]
                ?.split(RECENT_DELIMITER)
                ?.filter { it.isNotBlank() }
                ?.toMutableList()
                ?: mutableListOf()
            current.remove(emoji)
            current.add(0, emoji)
            while (current.size > maxItems) {
                current.removeAt(current.lastIndex)
            }
            prefs[recentEmojiKey] = current.joinToString(RECENT_DELIMITER)
        }
    }

    companion object {
        const val MAX_RECENT_EMOJI = 32
        private const val RECENT_DELIMITER = "\u001F"
    }
}

/** Pure helper for unit tests without DataStore. */
object LanguageRules {
    fun withLanguageEnabled(
        enabled: Set<InputLanguage>,
        language: InputLanguage,
        enable: Boolean,
    ): Set<InputLanguage>? {
        val next = if (enable) enabled + language else enabled - language
        return next.takeIf { it.isNotEmpty() }
    }
}
