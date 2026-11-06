package com.freekeyboardballoutcode.ime.input

import android.view.KeyEvent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class RawKeyCodesTest {
    @Test
    fun spaceAndPunctuation_mapToKeyEvents() {
        assertEquals(KeyEvent.KEYCODE_SPACE, RawKeyCodes.strokeFor(' ')!!.keyCode)
        assertEquals(KeyEvent.KEYCODE_PERIOD, RawKeyCodes.strokeFor('.')!!.keyCode)
        assertEquals(KeyEvent.KEYCODE_COMMA, RawKeyCodes.strokeFor(',')!!.keyCode)
        val q = RawKeyCodes.strokeFor('?')!!
        assertEquals(KeyEvent.KEYCODE_SLASH, q.keyCode)
        assertEquals(KeyEvent.META_SHIFT_ON, q.metaState)
    }

    @Test
    fun letters_useShiftMetaForUppercase() {
        val lower = RawKeyCodes.strokeFor('a')!!
        assertEquals(KeyEvent.KEYCODE_A, lower.keyCode)
        assertEquals(0, lower.metaState)
        val upper = RawKeyCodes.strokeFor('A')!!
        assertEquals(KeyEvent.KEYCODE_A, upper.keyCode)
        assertEquals(KeyEvent.META_SHIFT_ON, upper.metaState)
    }

    @Test
    fun nonAscii_returnsNull() {
        assertNull(RawKeyCodes.strokeFor('ж'))
        assertNull(RawKeyCodes.strokeFor('§'))
        assertNotNull(RawKeyCodes.strokeFor('!'))
    }
}
