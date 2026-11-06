package com.freekeyboardballoutcode.data

import org.junit.Assert.assertEquals
import org.junit.Test

class RecentEmojiStoreTest {
    @Test
    fun upsertMovesToFrontAndCaps() {
        val base = (1..5).map { "e$it" }
        val next = RecentEmojiStore.upsert(base, "e3", maxItems = 4)
        assertEquals(listOf("e3", "e1", "e2", "e4"), next)
    }
}
