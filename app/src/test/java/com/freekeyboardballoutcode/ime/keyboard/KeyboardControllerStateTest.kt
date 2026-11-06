package com.freekeyboardballoutcode.ime.keyboard

import com.freekeyboardballoutcode.data.KeyboardLayer
import com.freekeyboardballoutcode.data.ShiftMode
import com.freekeyboardballoutcode.ime.input.InputConnectionFacade
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class KeyboardControllerStateTest {
    private fun controller() = KeyboardController(
        InputConnectionFacade(
            connectionProvider = { null },
            editorInfoProvider = { null },
        ),
    )

    @Test
    fun onStartInput_resetsSymbolsAndEmojiLayers() {
        val c = controller()
        c.onKey(KeyDefinition("symbols", "?123", action = KeyAction.LAYER_SYMBOLS))
        assertEquals(KeyboardLayer.SYMBOLS, c.state.value.layer)
        c.onStartInput()
        assertEquals(KeyboardLayer.LETTERS, c.state.value.layer)
        assertFalse(c.state.value.symbolsAlt)

        c.onKey(KeyDefinition("emoji", "☺", action = KeyAction.EMOJI))
        assertEquals(KeyboardLayer.EMOJI, c.state.value.layer)
        c.onStartInput()
        assertEquals(KeyboardLayer.LETTERS, c.state.value.layer)
    }

    @Test
    fun shiftLongPress_onSymbols_togglesAltPage() {
        val c = controller()
        c.onKey(KeyDefinition("symbols", "?123", action = KeyAction.LAYER_SYMBOLS))
        assertFalse(c.state.value.symbolsAlt)
        c.onShiftLongPress()
        assertTrue(c.state.value.symbolsAlt)
        c.onShiftLongPress()
        assertFalse(c.state.value.symbolsAlt)
        assertEquals(ShiftMode.OFF, c.state.value.shift)
    }

    @Test
    fun shiftLongPress_onLetters_locksCaps() {
        val c = controller()
        assertEquals(KeyboardLayer.LETTERS, c.state.value.layer)
        c.onShiftLongPress()
        assertEquals(ShiftMode.CAPS, c.state.value.shift)
    }

    @Test
    fun noopKey_doesNotConsumeShiftOnce() {
        val c = controller()
        // Force ONCE via refresh path: empty field + autocap defaults to ONCE after start.
        c.onStartInput()
        assertEquals(ShiftMode.ONCE, c.state.value.shift)
        c.onKey(
            KeyDefinition(
                code = "num_pad",
                label = "",
                output = "",
                action = KeyAction.NOOP,
            ),
        )
        assertEquals(ShiftMode.ONCE, c.state.value.shift)
    }
}
