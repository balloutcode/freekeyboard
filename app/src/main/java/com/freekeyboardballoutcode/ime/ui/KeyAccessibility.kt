package com.freekeyboardballoutcode.ime.ui

import com.freekeyboardballoutcode.data.ShiftMode
import com.freekeyboardballoutcode.ime.input.EnterKeyStyle
import com.freekeyboardballoutcode.ime.keyboard.KeyAction
import com.freekeyboardballoutcode.ime.keyboard.KeyDefinition

/**
 * TalkBack / Switch Access content description for a key (A20-001/A20-004).
 *
 * Icon-only / special keys show a raw glyph (e.g. "⇧", "⌫", "🌐") as their visual
 * [label]; that glyph is a poor screen-reader string, so special keys get a
 * descriptive Russian name instead. Plain letter/digit/punctuation keys keep
 * their visible [label] — the glyph itself is already meaningful when read aloud.
 *
 * Returns `null` for [KeyAction.NOOP] (e.g. RU number-row pad spacer): that key
 * is visually present but must stay invisible to accessibility services, since
 * it never commits text or changes state.
 */
object KeyAccessibility {
    fun describe(
        key: KeyDefinition,
        shift: ShiftMode,
        enterStyle: EnterKeyStyle,
        label: String,
    ): String? = when (key.action) {
        KeyAction.NOOP -> null
        KeyAction.SHIFT -> when (shift) {
            ShiftMode.OFF -> "Shift"
            ShiftMode.ONCE -> "Shift, следующая буква заглавная"
            ShiftMode.CAPS -> "Caps Lock включён"
        }
        KeyAction.BACKSPACE -> "Удалить"
        KeyAction.ENTER -> when (enterStyle) {
            EnterKeyStyle.SEND -> "Отправить"
            EnterKeyStyle.SEARCH -> "Искать"
            EnterKeyStyle.DONE -> "Готово"
            EnterKeyStyle.GO -> "Перейти"
            EnterKeyStyle.NEXT -> "Далее"
            EnterKeyStyle.ENTER -> "Новая строка"
        }
        KeyAction.EMOJI -> "Эмодзи"
        KeyAction.LANGUAGE -> "Смена языка"
        KeyAction.SPACE -> "Пробел, текущий язык: $label"
        KeyAction.LAYER_SYMBOLS -> "Символы"
        KeyAction.LAYER_LETTERS -> "Буквы"
        KeyAction.COMMIT -> label
    }
}
