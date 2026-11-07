package com.freekeyboardballoutcode.ime.keyboard

import com.freekeyboardballoutcode.ime.keyboard.layouts.RuJcuiken
import org.junit.Assert.assertTrue
import org.junit.Test

class RuVariantsTest {
    private fun variantsFor(letter: String): List<String> {
        return RuJcuiken.layout.rows
            .flatMap { it.keys }
            .first { it.output == letter }
            .variants
    }

    @Test
    fun yeHasYo() {
        assertTrue(variantsFor("е").contains("ё"))
    }

    @Test
    fun softSignHasHardSign() {
        assertTrue(variantsFor("ь").contains("ъ"))
    }

    @Test
    fun iHasUkrainianNeighbors() {
        val v = variantsFor("и")
        assertTrue(v.contains("і"))
        assertTrue(v.contains("ї"))
    }

    @Test
    fun geHasGheWithUpturn() {
        assertTrue(variantsFor("г").contains("ґ"))
    }

    @Test
    fun uHasShortU() {
        assertTrue(variantsFor("у").contains("ў"))
    }
}
