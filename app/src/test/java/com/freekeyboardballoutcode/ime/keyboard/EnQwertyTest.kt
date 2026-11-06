package com.freekeyboardballoutcode.ime.keyboard

import com.freekeyboardballoutcode.ime.keyboard.layouts.EnQwerty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EnQwertyTest {
    @Test
    fun qwertyContainsExpectedHomeRow() {
        val labels = EnQwerty.layout.rows[1].keys.map { it.label }
        assertEquals(listOf("a", "s", "d", "f", "g", "h", "j", "k", "l"), labels)
    }

    @Test
    fun shiftedLetterUppercases() {
        val key = EnQwerty.layout.rows[1].keys.first { it.label == "a" }
        assertEquals("A", EnQwerty.shiftedLabel(key, caps = true))
        assertEquals("a", EnQwerty.shiftedLabel(key, caps = false))
    }

    @Test
    fun eHasVariants() {
        val e = EnQwerty.layout.rows[0].keys.first { it.label == "e" }
        assertTrue(e.variants.contains("é"))
    }
}
