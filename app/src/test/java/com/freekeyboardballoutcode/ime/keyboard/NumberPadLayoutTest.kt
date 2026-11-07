package com.freekeyboardballoutcode.ime.keyboard

import com.freekeyboardballoutcode.ime.keyboard.layouts.NumberPad
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NumberPadLayoutTest {
    @Test
    fun numberPadContainsDigitsWithoutAbcOrSpace() {
        val keys = NumberPad.layout.rows.flatMap { it.keys }
        val outputs = keys.map { it.output }.toSet()
        (0..9).forEach { d -> assertTrue(outputs.contains(d.toString())) }
        assertTrue(keys.any { it.action == KeyAction.BACKSPACE })
        assertTrue(keys.any { it.action == KeyAction.ENTER })
        assertFalse(keys.any { it.action == KeyAction.LAYER_LETTERS })
        assertFalse(keys.any { it.action == KeyAction.SPACE })
        assertEquals("number_pad", NumberPad.layout.id)
    }

    @Test
    fun pinLayoutIdAndDigits() {
        val pin = NumberPad.numberLayout(pinStyle = true)
        assertEquals("number_pad_pin", pin.id)
        val outputs = pin.rows.flatMap { it.keys }.map { it.output }.toSet()
        (0..9).forEach { d -> assertTrue(outputs.contains(d.toString())) }
    }

    @Test
    fun decimalAndSignedExtras() {
        val decimal = NumberPad.numberLayout(allowDecimal = true)
        assertTrue(decimal.rows.flatMap { it.keys }.any { it.output == "." })

        val signed = NumberPad.numberLayout(allowSigned = true)
        assertTrue(signed.rows.flatMap { it.keys }.any { it.output == "-" })

        val both = NumberPad.numberLayout(allowDecimal = true, allowSigned = true)
        val outs = both.rows.flatMap { it.keys }.map { it.output }.toSet()
        assertTrue(outs.contains("."))
        assertTrue(outs.contains("-"))
    }

    @Test
    fun phonePadHasStarHashAndHints() {
        val keys = NumberPad.phoneLayout.rows.flatMap { it.keys }
        assertEquals("phone_pad", NumberPad.phoneLayout.id)
        assertTrue(keys.any { it.output == "*" })
        assertTrue(keys.any { it.output == "#" })
        assertTrue(keys.any { it.hint == "ABC" })
        assertFalse(keys.any { it.action == KeyAction.LAYER_LETTERS })
    }
}
