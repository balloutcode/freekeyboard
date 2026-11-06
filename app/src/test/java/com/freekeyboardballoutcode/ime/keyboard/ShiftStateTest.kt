package com.freekeyboardballoutcode.ime.keyboard

import com.freekeyboardballoutcode.data.ShiftMode
import org.junit.Assert.assertEquals
import org.junit.Test

class ShiftStateTest {
    @Test
    fun singleTap_offToOnce() {
        assertEquals(
            ShiftMode.ONCE,
            KeyboardController.shiftAfterTap(ShiftMode.OFF, doubleTap = false),
        )
    }

    @Test
    fun singleTap_onceToOff() {
        assertEquals(
            ShiftMode.OFF,
            KeyboardController.shiftAfterTap(ShiftMode.ONCE, doubleTap = false),
        )
    }

    @Test
    fun doubleTap_onceToCaps() {
        assertEquals(
            ShiftMode.CAPS,
            KeyboardController.shiftAfterTap(ShiftMode.ONCE, doubleTap = true),
        )
    }

    @Test
    fun tap_capsToOff() {
        assertEquals(
            ShiftMode.OFF,
            KeyboardController.shiftAfterTap(ShiftMode.CAPS, doubleTap = false),
        )
        assertEquals(
            ShiftMode.OFF,
            KeyboardController.shiftAfterTap(ShiftMode.CAPS, doubleTap = true),
        )
    }

    @Test
    fun doubleSpace_onlyAfterLetterToken() {
        assertEquals(true, KeyboardController.shouldInsertPeriodOnDoubleSpace("hello "))
        assertEquals(false, KeyboardController.shouldInsertPeriodOnDoubleSpace("v2 "))
        assertEquals(false, KeyboardController.shouldInsertPeriodOnDoubleSpace("hello) "))
        assertEquals(false, KeyboardController.shouldInsertPeriodOnDoubleSpace("https://a "))
        assertEquals(false, KeyboardController.shouldInsertPeriodOnDoubleSpace("a"))
    }
}
