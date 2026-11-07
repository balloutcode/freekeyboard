package com.freekeyboardballoutcode.ime.input

import android.view.KeyEvent

/**
 * Maps printable characters to hardware key events for [InputType.TYPE_NULL] editors
 * that ignore [android.view.inputmethod.InputConnection.commitText].
 *
 * Returns `metaState` + `keyCode`, or null when no US-QWERTY key event exists
 * (emoji, Cyrillic, etc. — callers should fall back to commitText).
 */
object RawKeyCodes {
    data class KeyStroke(val keyCode: Int, val metaState: Int = 0)

    /**
     * What a [TYPE_NULL]-style raw-key editor should receive for one character:
     * either a hardware key stroke, or (when no US-QWERTY keycode exists — e.g.
     * Cyrillic, emoji) the literal Unicode text to send via a single
     * `ACTION_MULTIPLE` / `KEYCODE_UNKNOWN` [android.view.KeyEvent] (A16-005).
     * Callers MUST NOT fall back to `InputConnection.commitText` for raw-key
     * editors: some TYPE_NULL hosts ignore `commitText` entirely.
     */
    sealed interface Action {
        data class Stroke(val keyCode: Int, val metaState: Int = 0) : Action
        data class Characters(val text: String) : Action
    }

    fun actionFor(char: Char): Action {
        val stroke = strokeFor(char)
        return if (stroke != null) {
            Action.Stroke(stroke.keyCode, stroke.metaState)
        } else {
            Action.Characters(char.toString())
        }
    }

    fun strokeFor(char: Char): KeyStroke? {
        if (char in 'a'..'z') {
            return KeyStroke(KeyEvent.KEYCODE_A + (char - 'a'))
        }
        if (char in 'A'..'Z') {
            return KeyStroke(KeyEvent.KEYCODE_A + (char - 'A'), KeyEvent.META_SHIFT_ON)
        }
        if (char in '0'..'9') {
            return KeyStroke(KeyEvent.KEYCODE_0 + (char - '0'))
        }
        return when (char) {
            ' ' -> KeyStroke(KeyEvent.KEYCODE_SPACE)
            '\n' -> KeyStroke(KeyEvent.KEYCODE_ENTER)
            '\t' -> KeyStroke(KeyEvent.KEYCODE_TAB)
            '.' -> KeyStroke(KeyEvent.KEYCODE_PERIOD)
            ',' -> KeyStroke(KeyEvent.KEYCODE_COMMA)
            '/' -> KeyStroke(KeyEvent.KEYCODE_SLASH)
            '\\' -> KeyStroke(KeyEvent.KEYCODE_BACKSLASH)
            ';' -> KeyStroke(KeyEvent.KEYCODE_SEMICOLON)
            '\'' -> KeyStroke(KeyEvent.KEYCODE_APOSTROPHE)
            '[' -> KeyStroke(KeyEvent.KEYCODE_LEFT_BRACKET)
            ']' -> KeyStroke(KeyEvent.KEYCODE_RIGHT_BRACKET)
            '-' -> KeyStroke(KeyEvent.KEYCODE_MINUS)
            '=' -> KeyStroke(KeyEvent.KEYCODE_EQUALS)
            '`' -> KeyStroke(KeyEvent.KEYCODE_GRAVE)
            '!' -> KeyStroke(KeyEvent.KEYCODE_1, KeyEvent.META_SHIFT_ON)
            '@' -> KeyStroke(KeyEvent.KEYCODE_2, KeyEvent.META_SHIFT_ON)
            '#' -> KeyStroke(KeyEvent.KEYCODE_3, KeyEvent.META_SHIFT_ON)
            '$' -> KeyStroke(KeyEvent.KEYCODE_4, KeyEvent.META_SHIFT_ON)
            '%' -> KeyStroke(KeyEvent.KEYCODE_5, KeyEvent.META_SHIFT_ON)
            '^' -> KeyStroke(KeyEvent.KEYCODE_6, KeyEvent.META_SHIFT_ON)
            '&' -> KeyStroke(KeyEvent.KEYCODE_7, KeyEvent.META_SHIFT_ON)
            '*' -> KeyStroke(KeyEvent.KEYCODE_8, KeyEvent.META_SHIFT_ON)
            '(' -> KeyStroke(KeyEvent.KEYCODE_9, KeyEvent.META_SHIFT_ON)
            ')' -> KeyStroke(KeyEvent.KEYCODE_0, KeyEvent.META_SHIFT_ON)
            '_' -> KeyStroke(KeyEvent.KEYCODE_MINUS, KeyEvent.META_SHIFT_ON)
            '+' -> KeyStroke(KeyEvent.KEYCODE_EQUALS, KeyEvent.META_SHIFT_ON)
            '{' -> KeyStroke(KeyEvent.KEYCODE_LEFT_BRACKET, KeyEvent.META_SHIFT_ON)
            '}' -> KeyStroke(KeyEvent.KEYCODE_RIGHT_BRACKET, KeyEvent.META_SHIFT_ON)
            '|' -> KeyStroke(KeyEvent.KEYCODE_BACKSLASH, KeyEvent.META_SHIFT_ON)
            ':' -> KeyStroke(KeyEvent.KEYCODE_SEMICOLON, KeyEvent.META_SHIFT_ON)
            '"' -> KeyStroke(KeyEvent.KEYCODE_APOSTROPHE, KeyEvent.META_SHIFT_ON)
            '<' -> KeyStroke(KeyEvent.KEYCODE_COMMA, KeyEvent.META_SHIFT_ON)
            '>' -> KeyStroke(KeyEvent.KEYCODE_PERIOD, KeyEvent.META_SHIFT_ON)
            '?' -> KeyStroke(KeyEvent.KEYCODE_SLASH, KeyEvent.META_SHIFT_ON)
            '~' -> KeyStroke(KeyEvent.KEYCODE_GRAVE, KeyEvent.META_SHIFT_ON)
            else -> null
        }
    }
}
