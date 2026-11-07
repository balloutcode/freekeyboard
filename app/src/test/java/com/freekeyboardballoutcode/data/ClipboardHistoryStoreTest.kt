package com.freekeyboardballoutcode.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ClipboardHistoryStoreTest {
    @Test
    fun pruneExpired_keepsPinnedAndFresh() {
        val now = 1_000_000L
        val fresh = ClipboardEntry("1", "a", pinned = false, createdAtMs = now - 1_000)
        val old = ClipboardEntry("2", "b", pinned = false, createdAtMs = now - ClipboardHistoryStore.TTL_MS - 1)
        val pinnedOld = ClipboardEntry("3", "c", pinned = true, createdAtMs = now - ClipboardHistoryStore.TTL_MS - 1)
        val kept = ClipboardHistoryStore.pruneExpired(listOf(fresh, old, pinnedOld), now)
        assertEquals(listOf(fresh, pinnedOld), kept)
    }

    @Test
    fun encodeDecode_roundTrip() {
        val entries = listOf(
            ClipboardEntry("id1", "hello", pinned = true, createdAtMs = 42),
            ClipboardEntry("id2", "мир", pinned = false, createdAtMs = 43),
        )
        val decoded = ClipboardHistoryStore.decode(ClipboardHistoryStore.encode(entries))
        assertEquals(entries, decoded)
        assertTrue(decoded[1].text == "мир")
    }
}
