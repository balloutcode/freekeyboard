package com.freekeyboardballoutcode.ime.input

import org.junit.Assert.assertEquals
import org.junit.Test

class WordDeleteTest {
    @Test
    fun empty_returnsZero() {
        assertEquals(0, WordDelete.codePointsToDelete(""))
    }

    @Test
    fun deletesWordAndTrailingSpaces() {
        assertEquals(6, WordDelete.codePointsToDelete("hello ")) // "hello "
        assertEquals(5, WordDelete.codePointsToDelete("hello"))
        assertEquals(7, WordDelete.codePointsToDelete("hi  there  ")) // "there  "
    }

    @Test
    fun emoji_countsCodePoints() {
        // "hi " + thumbs-up emoji (1 code point, 2 UTF-16 chars) + space
        val before = "hi \uD83D\uDC4D "
        assertEquals(2, WordDelete.codePointsToDelete(before)) // emoji + space
    }
}
