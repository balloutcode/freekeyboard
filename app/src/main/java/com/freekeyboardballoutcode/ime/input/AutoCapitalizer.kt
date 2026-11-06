package com.freekeyboardballoutcode.ime.input

import android.text.InputType
import android.view.inputmethod.EditorInfo

object AutoCapitalizer {
    // Include typographic quotes offered on long-press comma/period keys («» „ “ ” ‘ ’).
    private val sentenceEnd = Regex("""[.!?…]["')\]»“”„‘’]*\s+$""")

    fun shouldCapitalize(
        textBeforeCursor: CharSequence?,
        editorInfo: EditorInfo?,
    ): Boolean {
        if (editorInfo != null && isPassword(editorInfo)) return false

        val flags = editorInfo?.inputType ?: 0
        val klass = flags and InputType.TYPE_MASK_CLASS
        if (klass != 0 && klass != InputType.TYPE_CLASS_TEXT) return false

        val capCharacters = flags and InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS != 0
        if (capCharacters) return true

        val capWords = flags and InputType.TYPE_TEXT_FLAG_CAP_WORDS != 0
        val capSentences = flags and InputType.TYPE_TEXT_FLAG_CAP_SENTENCES != 0
        // Gboard-like default for plain text: capitalize sentence starts
        val sentenceMode = capSentences || (!capWords && !capCharacters)

        val before = textBeforeCursor?.toString().orEmpty()
        if (before.isEmpty() || before.isBlank()) return true

        if (capWords) {
            val last = before.lastOrNull()
            if (last == null || last.isWhitespace()) return true
        }

        if (sentenceMode) {
            if (sentenceEnd.containsMatchIn(before)) return true
            if (before.endsWith("\n") || before.endsWith("\r\n")) return true
        }
        return false
    }

    private fun isPassword(editorInfo: EditorInfo): Boolean {
        val variation = editorInfo.inputType and InputType.TYPE_MASK_VARIATION
        val klass = editorInfo.inputType and InputType.TYPE_MASK_CLASS
        return klass == InputType.TYPE_CLASS_TEXT && (
            variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
                variation == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
            )
    }
}
