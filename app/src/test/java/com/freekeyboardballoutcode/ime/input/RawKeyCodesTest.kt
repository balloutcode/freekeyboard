package com.freekeyboardballoutcode.ime.input

import android.view.KeyEvent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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

    // A16-005: TYPE_NULL Cyrillic (and other no-keycode chars) must resolve to a
    // raw Unicode `Characters` action — never a value meant for `commitText`.
    @Test
    fun cyrillic_resolvesToCharactersAction_notStroke() {
        val action = RawKeyCodes.actionFor('ж')
        assertTrue(action is RawKeyCodes.Action.Characters)
        assertEquals("ж", (action as RawKeyCodes.Action.Characters).text)
    }

    @Test
    fun emoji_resolvesToCharactersAction() {
        val action = RawKeyCodes.actionFor('\uD83D') // high surrogate of an emoji
        assertTrue(action is RawKeyCodes.Action.Characters)
    }

    @Test
    fun asciiLetter_resolvesToStrokeAction() {
        val action = RawKeyCodes.actionFor('q')
        assertTrue(action is RawKeyCodes.Action.Stroke)
        assertEquals(KeyEvent.KEYCODE_Q, (action as RawKeyCodes.Action.Stroke).keyCode)
    }
}
