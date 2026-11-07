package com.freekeyboardballoutcode.ime.ui

import org.junit.Assert.assertTrue
import org.junit.Test

class EmojiSearchTest {
    @Test
    fun heartQueryFindsHeartEmoji() {
        val pool = listOf("😀", "❤️", "🐶", "🍕")
        val hits = EmojiSearch.filter(pool, "heart")
        assertTrue(hits.contains("❤️"))
    }

    @Test
    fun russianQueryFindsHeart() {
        val pool = listOf("😀", "❤️", "👍")
        val hits = EmojiSearch.filter(pool, "сердце")
        assertTrue(hits.contains("❤️"))
    }

    @Test
    fun blankQueryReturnsAll() {
        val pool = listOf("😀", "❤️")
        assertTrue(EmojiSearch.filter(pool, "  ") == pool)
    }
}
