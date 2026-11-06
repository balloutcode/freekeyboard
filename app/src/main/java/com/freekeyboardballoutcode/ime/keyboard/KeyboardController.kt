package com.freekeyboardballoutcode.ime.keyboard

import android.os.SystemClock
import com.freekeyboardballoutcode.data.InputLanguage
import com.freekeyboardballoutcode.data.KeyboardLayer
import com.freekeyboardballoutcode.data.ShiftMode
import com.freekeyboardballoutcode.data.ThemePreference
import com.freekeyboardballoutcode.data.UserPreferences
import com.freekeyboardballoutcode.ime.input.AutoCapitalizer
import com.freekeyboardballoutcode.ime.input.EditorInfoInterpreter
import com.freekeyboardballoutcode.ime.input.EnterKeyStyle
import com.freekeyboardballoutcode.ime.input.InputConnectionFacade
import com.freekeyboardballoutcode.ime.keyboard.layouts.EnQwerty
import com.freekeyboardballoutcode.ime.keyboard.layouts.RuJcuiken
import com.freekeyboardballoutcode.ime.keyboard.layouts.Symbols
import com.freekeyboardballoutcode.ime.suggest.SuggestionEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class KeyboardUiState(
    val theme: ThemePreference = ThemePreference.LIGHT,
    val keyBorders: Boolean = true,
    val language: InputLanguage = InputLanguage.EN,
    val enabledLanguages: Set<InputLanguage> = setOf(InputLanguage.EN, InputLanguage.RU),
    val layer: KeyboardLayer = KeyboardLayer.LETTERS,
    val shift: ShiftMode = ShiftMode.OFF,
    val symbolsAlt: Boolean = false,
    val showLanguagePicker: Boolean = false,
    val recentEmoji: List<String> = emptyList(),
    val suggestions: List<String> = emptyList(),
    val enterStyle: EnterKeyStyle = EnterKeyStyle.ENTER,
    val numberRow: Boolean = false,
    val showEmojiKey: Boolean = true,
    val soundOnKeypress: Boolean = false,
    val hapticOnKeypress: Boolean = true,
    val popupOnKeypress: Boolean = true,
    val longPressForSymbols: Boolean = true,
    val longPressDelayMs: Int = 300,
    val showSuggestionStrip: Boolean = true,
    val nextWordSuggestions: Boolean = true,
    val blockOffensiveWords: Boolean = true,
    val autoCapitalization: Boolean = true,
    val doubleSpacePeriod: Boolean = true,
) {
    val lettersLayout: KeyboardLayout
        get() {
            val base = when (language) {
                InputLanguage.EN -> EnQwerty.layout
                InputLanguage.RU -> RuJcuiken.layout
            }
            return LayoutOptions.apply(
                base,
                numberRow = numberRow,
                showEmojiKey = showEmojiKey,
                longPressForSymbols = longPressForSymbols,
                showLanguageKey = enabledLanguages.size >= 2,
            )
        }

    val activeLayout: KeyboardLayout
        get() = when (layer) {
            KeyboardLayer.LETTERS -> lettersLayout
            KeyboardLayer.SYMBOLS -> {
                val base = if (symbolsAlt) Symbols.altLayout else Symbols.layout
                LayoutOptions.apply(
                    base,
                    numberRow = false,
                    showEmojiKey = showEmojiKey,
                    longPressForSymbols = longPressForSymbols,
                    showLanguageKey = enabledLanguages.size >= 2,
                )
            }
            KeyboardLayer.EMOJI -> lettersLayout
        }

    val isCaps: Boolean
        get() = shift == ShiftMode.ONCE || shift == ShiftMode.CAPS
}

class KeyboardController(
    private val input: InputConnectionFacade,
    private val onLanguageCommitted: suspend (InputLanguage) -> Unit = {},
    private val onEmojiCommitted: suspend (String) -> Unit = {},
) {
    private val _state = MutableStateFlow(KeyboardUiState())
    val state: StateFlow<KeyboardUiState> = _state.asStateFlow()
    private var lastShiftTapUptimeMs: Long = 0L

    fun applyPreferences(prefs: UserPreferences) {
        _state.update { current ->
            val enabled = prefs.enabledLanguages.ifEmpty { setOf(InputLanguage.EN) }
            val language = when {
                prefs.lastActiveLanguage in enabled -> prefs.lastActiveLanguage
                current.language in enabled -> current.language
                else -> preferredLanguage(enabled)
            }
            current.copy(
                theme = prefs.theme,
                keyBorders = prefs.keyBorders,
                language = language,
                enabledLanguages = enabled,
                recentEmoji = prefs.recentEmoji,
                numberRow = prefs.numberRow,
                showEmojiKey = prefs.showEmojiKey,
                soundOnKeypress = prefs.soundOnKeypress,
                hapticOnKeypress = prefs.hapticOnKeypress,
                popupOnKeypress = prefs.popupOnKeypress,
                longPressForSymbols = prefs.longPressForSymbols,
                longPressDelayMs = prefs.longPressDelayMs,
                showSuggestionStrip = prefs.showSuggestionStrip,
                nextWordSuggestions = prefs.nextWordSuggestions,
                blockOffensiveWords = prefs.blockOffensiveWords,
                autoCapitalization = prefs.autoCapitalization,
                doubleSpacePeriod = prefs.doubleSpacePeriod,
            )
        }
    }

    fun onStartInput() {
        // New field / restart: leave Symbols/Emoji so the next editor always opens on letters.
        _state.update {
            it.copy(
                enterStyle = input.enterKeyStyle(),
                shift = ShiftMode.OFF,
                layer = KeyboardLayer.LETTERS,
                symbolsAlt = false,
                showLanguagePicker = false,
            )
        }
        refreshAfterEdit(consumeOnce = false)
    }

    fun onUpdateSelection() {
        refreshAfterEdit(consumeOnce = false)
    }

    fun onKey(key: KeyDefinition) {
        when (key.action) {
            KeyAction.NOOP -> Unit
            KeyAction.COMMIT -> commitCharacter(key)
            KeyAction.SPACE -> onSpace()
            KeyAction.BACKSPACE -> onBackspace(byWord = false)
            KeyAction.ENTER -> {
                input.performEnter()
                refreshAfterEdit(consumeOnce = true)
            }
            KeyAction.SHIFT -> onShiftTap()
            KeyAction.LAYER_SYMBOLS -> {
                _state.update {
                    it.copy(
                        layer = KeyboardLayer.SYMBOLS,
                        symbolsAlt = false,
                        shift = ShiftMode.OFF,
                        suggestions = emptyList(),
                    )
                }
            }
            KeyAction.LAYER_LETTERS -> {
                _state.update {
                    it.copy(layer = KeyboardLayer.LETTERS, symbolsAlt = false, shift = ShiftMode.OFF)
                }
                refreshAfterEdit(consumeOnce = false)
            }
            KeyAction.EMOJI -> _state.update { it.copy(layer = KeyboardLayer.EMOJI) }
            KeyAction.LANGUAGE -> toggleOrPickLanguage()
        }
    }

    fun onBackspace(byWord: Boolean = false) {
        if (byWord) input.deleteWordBackward() else input.deleteBackward()
        refreshAfterEdit(consumeOnce = false)
    }

    fun onVariantSelected(text: String) {
        input.commitText(text)
        refreshAfterEdit(consumeOnce = true)
    }

    fun onEmojiSelected(emoji: String) {
        input.commitText(emoji)
        _state.update {
            val recent = (listOf(emoji) + it.recentEmoji.filterNot { e -> e == emoji }).take(32)
            it.copy(recentEmoji = recent)
        }
        refreshAfterEdit(consumeOnce = false)
    }

    fun acceptSuggestion(word: String) {
        val before = input.textBeforeCursor()
        val prefix = SuggestionEngine.currentPrefix(before)
        if (prefix.isNotEmpty()) {
            input.deleteBeforeCursor(prefix.codePointCount(0, prefix.length))
        }
        input.commitText("$word ")
        refreshAfterEdit(consumeOnce = true)
    }

    fun closeEmoji() {
        _state.update { it.copy(layer = KeyboardLayer.LETTERS) }
        refreshAfterEdit(consumeOnce = false)
    }

    fun dismissLanguagePicker() {
        _state.update { it.copy(showLanguagePicker = false) }
    }

    fun selectLanguage(language: InputLanguage) {
        if (language !in _state.value.enabledLanguages) return
        _state.update {
            it.copy(language = language, showLanguagePicker = false, layer = KeyboardLayer.LETTERS)
        }
        refreshAfterEdit(consumeOnce = false)
    }

    fun currentLanguage(): InputLanguage = _state.value.language

    private fun onSpace() {
        val prefs = _state.value
        if (prefs.doubleSpacePeriod && shouldInsertPeriodOnDoubleSpace(input.textBeforeCursor())) {
            input.deleteBeforeCursor(1)
            input.commitText(". ")
            refreshAfterEdit(consumeOnce = true)
            return
        }
        input.commitText(" ")
        refreshAfterEdit(consumeOnce = true)
    }

    private fun commitCharacter(key: KeyDefinition) {
        if (key.output.isEmpty()) return
        val caps = _state.value.isCaps
        val text = when {
            key.action != KeyAction.COMMIT -> key.output
            key.output.length == 1 && key.output.first().isLetter() && caps -> key.output.uppercase()
            else -> key.output
        }
        input.commitText(text)
        refreshAfterEdit(consumeOnce = true)
    }

    /**
     * Gboard Shift:
     * - tap: OFF → ONCE (one capital); ONCE → OFF; CAPS → OFF
     * - double-tap: ONCE → CAPS (lock all capitals)
     * Long-press also locks CAPS via [onShiftLongPress].
     */
    private fun onShiftTap() {
        val current = _state.value
        if (current.layer == KeyboardLayer.SYMBOLS) {
            _state.update { it.copy(symbolsAlt = !it.symbolsAlt) }
            return
        }
        val now = SystemClock.uptimeMillis()
        val doubleTap = now - lastShiftTapUptimeMs <= SHIFT_DOUBLE_TAP_MS
        lastShiftTapUptimeMs = now
        _state.update {
            it.copy(shift = shiftAfterTap(it.shift, doubleTap = doubleTap))
        }
    }

    /**
     * Hold Shift:
     * - Letters → Caps Lock (same end-state as Gboard double-tap).
     * - Symbols → toggle alt page (same as tap; long-press must not be a no-op).
     */
    fun onShiftLongPress() {
        val current = _state.value
        if (current.layer == KeyboardLayer.SYMBOLS) {
            _state.update { it.copy(symbolsAlt = !it.symbolsAlt) }
            return
        }
        if (current.layer != KeyboardLayer.LETTERS) return
        lastShiftTapUptimeMs = 0L
        _state.update { it.copy(shift = ShiftMode.CAPS) }
    }

    private fun refreshAfterEdit(consumeOnce: Boolean) {
        val before = input.textBeforeCursor()
        val info = input.editorInfo()
        val st = _state.value
        val interpreter = EditorInfoInterpreter()
        val wantCaps = st.autoCapitalization && AutoCapitalizer.shouldCapitalize(before, info)
        val suggestions = if (
            !st.showSuggestionStrip ||
            st.layer != KeyboardLayer.LETTERS ||
            interpreter.suppressSuggestions(info)
        ) {
            emptyList()
        } else {
            SuggestionEngine.suggestions(
                textBeforeCursor = before,
                language = st.language,
                limit = 3,
                nextWordEnabled = st.nextWordSuggestions,
                blockOffensive = st.blockOffensiveWords,
                capitalizeNext = wantCaps,
            )
        }

        _state.update { current ->
            val shift = when {
                current.shift == ShiftMode.CAPS -> ShiftMode.CAPS
                wantCaps -> ShiftMode.ONCE
                consumeOnce -> ShiftMode.OFF
                current.shift == ShiftMode.ONCE -> ShiftMode.ONCE
                else -> ShiftMode.OFF
            }
            current.copy(
                shift = shift,
                suggestions = suggestions,
                enterStyle = input.enterKeyStyle(),
            )
        }
    }

    /** Short-press globe: cycle to the next enabled language immediately. */
    private fun toggleOrPickLanguage() {
        val enabled = _state.value.enabledLanguages.toList()
        if (enabled.size < 2) return
        val current = _state.value.language
        val index = enabled.indexOf(current).let { if (it < 0) 0 else it }
        val next = enabled[(index + 1) % enabled.size]
        _state.update { it.copy(language = next, layer = KeyboardLayer.LETTERS) }
        refreshAfterEdit(consumeOnce = false)
    }

    fun requestLanguagePicker() {
        if (_state.value.enabledLanguages.size < 2) return
        _state.update { it.copy(showLanguagePicker = true) }
    }

    companion object {
        const val SHIFT_DOUBLE_TAP_MS = 400L

        fun shiftAfterTap(current: ShiftMode, doubleTap: Boolean): ShiftMode = when (current) {
            ShiftMode.CAPS -> ShiftMode.OFF
            ShiftMode.OFF -> ShiftMode.ONCE
            ShiftMode.ONCE -> if (doubleTap) ShiftMode.CAPS else ShiftMode.OFF
        }

        fun preferredLanguage(enabled: Set<InputLanguage>): InputLanguage =
            listOf(InputLanguage.EN, InputLanguage.RU).firstOrNull { it in enabled }
                ?: enabled.minBy { it.name }

        /**
         * Double-space → period only after an all-letter token (not digits, URLs, or punctuation).
         */
        fun shouldInsertPeriodOnDoubleSpace(textBeforeCursor: CharSequence?): Boolean {
            val before = textBeforeCursor?.toString().orEmpty()
            if (!before.endsWith(" ") || before.length < 2) return false
            var i = before.length - 2
            while (i >= 0 && !before[i].isWhitespace()) i--
            val token = before.substring(i + 1, before.length - 1)
            return token.isNotEmpty() && token.all { it.isLetter() }
        }
    }
}
