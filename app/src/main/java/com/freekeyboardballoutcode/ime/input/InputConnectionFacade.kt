package com.freekeyboardballoutcode.ime.input

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

    private fun sendKeyChar(char: Char) {
        val stroke = RawKeyCodes.strokeFor(char)
        if (stroke != null) {
            sendDownUp(stroke.keyCode, stroke.metaState)
        } else {
            // Non-ASCII (emoji, Cyrillic, …): no hardware key — best-effort commitText.
            connectionProvider()?.commitText(char.toString(), 1)
        }
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
