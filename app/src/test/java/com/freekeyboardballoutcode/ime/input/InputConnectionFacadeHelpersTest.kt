package com.freekeyboardballoutcode.ime.input

import org.junit.Assert.assertEquals
import org.junit.Test

class InputConnectionFacadeHelpersTest {
    @Test
    fun utf16UnitsForCodePoints_handlesEmojiSurrogates() {
        // "hi" + grinning face (U+1F600 = 2 UTF-16 units) + "!"
        val text = "hi\uD83D\uDE00!"
        assertEquals(1, InputConnectionFacade.utf16UnitsForCodePoints(text, 1)) // "!"
        assertEquals(3, InputConnectionFacade.utf16UnitsForCodePoints(text, 2)) // emoji + "!"
        assertEquals(4, InputConnectionFacade.utf16UnitsForCodePoints(text, 3)) // "i" + emoji + "!"
    }

    @Test
    fun utf16UnitsForCodePoints_emptyAndOversize() {
        assertEquals(0, InputConnectionFacade.utf16UnitsForCodePoints("", 3))
        assertEquals(0, InputConnectionFacade.utf16UnitsForCodePoints("abc", 0))
        assertEquals(3, InputConnectionFacade.utf16UnitsForCodePoints("abc", 99))
    }
}
