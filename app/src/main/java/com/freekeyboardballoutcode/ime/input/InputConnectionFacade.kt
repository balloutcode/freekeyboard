package com.freekeyboardballoutcode.ime.input

import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager

class InputConnectionFacade(
    private val connectionProvider: () -> InputConnection?,
    private val editorInfoProvider: () -> EditorInfo?,
    private val interpreter: EditorInfoInterpreter = EditorInfoInterpreter(),
) {
    fun editorInfo(): EditorInfo? = editorInfoProvider()

    fun textBeforeCursor(n: Int = 64): CharSequence? =
        connectionProvider()?.getTextBeforeCursor(n, 0)

    fun commitText(text: String) {
        val ic = connectionProvider() ?: return
        val info = editorInfoProvider()
        if (interpreter.requiresRawKeys(info)) {
            text.forEach { sendKeyChar(it) }
            return
        }
        ic.commitText(text, 1)
    }

    fun deleteBackward() {
        val ic = connectionProvider() ?: return
        val selected = ic.getSelectedText(0)
        if (!selected.isNullOrEmpty()) {
            ic.commitText("", 1)
            return
        }
        if (!ic.deleteSurroundingTextInCodePoints(1, 0)) {
            ic.deleteSurroundingText(1, 0)
        }
    }

    /** Gboard-like hold-backspace: delete the previous whitespace run + word. */
    fun deleteWordBackward() {
        val ic = connectionProvider() ?: return
        // A21-012: group the (possibly multi-step) deletion into one batch edit so
        // hosts see a single atomic text change instead of several intermediate ones.
        ic.beginBatchEdit()
        try {
            val selected = ic.getSelectedText(0)
            if (!selected.isNullOrEmpty()) {
                ic.commitText("", 1)
                return
            }
            val before = ic.getTextBeforeCursor(64, 0)?.toString().orEmpty()
            val codePoints = WordDelete.codePointsToDelete(before)
            if (codePoints <= 0) return
            if (!ic.deleteSurroundingTextInCodePoints(codePoints, 0)) {
                // Fallback: UTF-16 length of the same segment, then char-by-char.
                val end = before.length
                var start = end
                while (start > 0 && before[start - 1].isWhitespace()) start--
                while (start > 0 && !before[start - 1].isWhitespace()) start--
                val utf16 = if (start < end) end - start else codePoints
                if (!ic.deleteSurroundingText(utf16, 0)) {
                    repeat(codePoints) { deleteBackward() }
                }
            }
        } finally {
            ic.endBatchEdit()
        }
    }

    fun deleteBeforeCursor(codePoints: Int) {
        if (codePoints <= 0) return
        val ic = connectionProvider() ?: return
        if (!ic.deleteSurroundingTextInCodePoints(codePoints, 0)) {
            ic.deleteSurroundingText(codePoints, 0)
        }
    }

    fun performEnter() {
        val ic = connectionProvider() ?: return
        val info = editorInfoProvider()
        if (interpreter.requiresRawKeys(info)) {
            sendDownUp(KeyEvent.KEYCODE_ENTER)
            return
        }
        val action = interpreter.editorAction(info)
        if (!ic.performEditorAction(action)) {
            ic.commitText("\n", 1)
        }
    }

    fun enterKeyStyle(): EnterKeyStyle = interpreter.enterKeyStyle(editorInfoProvider())

    /**
     * Move the caret by [delta] Unicode code points. Positive → right, negative → left.
     * Used by spacebar cursor swipe (Gboard parity).
     */
    fun moveCursorBy(delta: Int) {
        if (delta == 0) return
        val ic = connectionProvider() ?: return
        val info = editorInfoProvider()
        if (interpreter.requiresRawKeys(info)) {
            val keyCode = if (delta < 0) KeyEvent.KEYCODE_DPAD_LEFT else KeyEvent.KEYCODE_DPAD_RIGHT
            repeat(kotlin.math.abs(delta)) { sendDownUp(keyCode) }
            return
        }
        val before = ic.getTextBeforeCursor(10_000, 0)?.toString().orEmpty()
        val after = ic.getTextAfterCursor(10_000, 0)?.toString().orEmpty()
        val full = before + after
        val posCp = before.codePointCount(0, before.length)
        val totalCp = full.codePointCount(0, full.length)
        val newCp = (posCp + delta).coerceIn(0, totalCp)
        if (newCp != posCp) {
            val newPos = full.offsetByCodePoints(0, newCp)
            ic.setSelection(newPos, newPos)
        }
    }

    private fun sendKeyChar(char: Char) {
        when (val action = RawKeyCodes.actionFor(char)) {
            is RawKeyCodes.Action.Stroke -> sendDownUp(action.keyCode, action.metaState)
            is RawKeyCodes.Action.Characters -> sendCharacters(action.text)
        }
    }

    /**
     * A16-005: Cyrillic (and other chars without a US-QWERTY keycode) on a
     * TYPE_NULL / raw-key editor — send a single ACTION_MULTIPLE/KEYCODE_UNKNOWN
     * [KeyEvent] carrying the literal text, exactly the mechanism Android's own
     * raw-key IME path uses (never `commitText`, which TYPE_NULL hosts may ignore).
     */
    private fun sendCharacters(text: String) {
        val ic = connectionProvider() ?: return
        val now = System.currentTimeMillis()
        ic.sendKeyEvent(KeyEvent(now, text, KeyCharacterMap.VIRTUAL_KEYBOARD, 0))
    }

    private fun sendDownUp(keyCode: Int, metaState: Int = 0) {
        val ic = connectionProvider() ?: return
        val now = System.currentTimeMillis()
        ic.sendKeyEvent(KeyEvent(now, now, KeyEvent.ACTION_DOWN, keyCode, 0, metaState))
        ic.sendKeyEvent(KeyEvent(now, now, KeyEvent.ACTION_UP, keyCode, 0, metaState))
    }
}

fun InputMethodManager.openInputMethodPickerSafe() {
    showInputMethodPicker()
}
