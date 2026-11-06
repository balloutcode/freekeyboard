package com.freekeyboardballoutcode.ime.input

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AutoCapitalizerTest {
    @Test
    fun emptyField_capitalizes() {
        assertTrue(AutoCapitalizer.shouldCapitalize("", null))
        assertTrue(AutoCapitalizer.shouldCapitalize(null, null))
    }

    @Test
    fun afterPeriodAndSpace_capitalizes() {
        assertTrue(AutoCapitalizer.shouldCapitalize("Hello. ", null))
        assertTrue(AutoCapitalizer.shouldCapitalize("Hi! ", null))
        assertTrue(AutoCapitalizer.shouldCapitalize("Wow? ", null))
        assertTrue(AutoCapitalizer.shouldCapitalize("Said «yes». ", null))
        assertTrue(AutoCapitalizer.shouldCapitalize("Quote.” ", null))
    }

    @Test
    fun midSentence_doesNotCapitalize() {
        assertFalse(AutoCapitalizer.shouldCapitalize("Hello ", null))
        assertFalse(AutoCapitalizer.shouldCapitalize("Hello", null))
    }

    @Test
    fun afterNewline_capitalizes() {
        assertTrue(AutoCapitalizer.shouldCapitalize("Hello\n", null))
    }
}
