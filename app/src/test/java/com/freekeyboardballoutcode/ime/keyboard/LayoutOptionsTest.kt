package com.freekeyboardballoutcode.ime.keyboard

import com.freekeyboardballoutcode.ime.keyboard.layouts.EnQwerty
import com.freekeyboardballoutcode.ime.keyboard.layouts.RuJcuiken
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LayoutOptionsTest {
    @Test
    fun enNumberRow_hasTenDigits() {
        val layout = LayoutOptions.apply(
            EnQwerty.layout,
            numberRow = true,
            showEmojiKey = true,
            longPressForSymbols = true,
        )
        val top = layout.rows.first().keys
        assertEquals(10, top.size)
        assertEquals((1..9).map { it.toString() } + "0", top.map { it.label })
        assertTrue(top.all { it.action == KeyAction.COMMIT })
    }

    @Test
    fun ruNumberRow_padIsNoop() {
        val layout = LayoutOptions.apply(
            RuJcuiken.layout,
            numberRow = true,
            showEmojiKey = true,
            longPressForSymbols = true,
        )
        val top = layout.rows.first().keys
        assertEquals(11, top.size)
        val pad = top.last()
        assertEquals("num_pad", pad.code)
        assertEquals(KeyAction.NOOP, pad.action)
        assertEquals("", pad.output)
    }

    @Test
    fun numberRow_clearsDigitHintsOnLetters() {
        val layout = LayoutOptions.apply(
            EnQwerty.layout,
            numberRow = true,
            showEmojiKey = true,
            longPressForSymbols = true,
        )
        val letterRow = layout.rows[1].keys
        assertTrue(letterRow.none { it.hint != null && it.hint!!.all { c -> c.isDigit() } })
    }
}
