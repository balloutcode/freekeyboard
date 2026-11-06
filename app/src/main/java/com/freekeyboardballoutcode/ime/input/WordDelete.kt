package com.freekeyboardballoutcode.ime.input

/**
 * How many UTF-16 code points to delete for a Gboard-like word backspace:
 * trailing whitespace run + the preceding word token.
 */
object WordDelete {
    fun codePointsToDelete(textBeforeCursor: String): Int {
        if (textBeforeCursor.isEmpty()) return 0
        var end = textBeforeCursor.length
        var start = end
        while (start > 0 && textBeforeCursor[start - 1].isWhitespace()) start--
        while (start > 0 && !textBeforeCursor[start - 1].isWhitespace()) start--
        if (start >= end) return 1
        val segment = textBeforeCursor.substring(start, end)
        return segment.codePointCount(0, segment.length).coerceAtLeast(1)
    }
}
