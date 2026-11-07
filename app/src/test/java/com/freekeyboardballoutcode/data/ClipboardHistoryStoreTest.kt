package com.freekeyboardballoutcode.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ClipboardHistoryStoreTest {
    @Test
    fun pruneExpired_keepsFreshUnpinned() {
        val now = 1_000_000L
        val fresh = ClipboardEntry("1", "a", pinned = false, createdAtMs = now - 1_000)
        val old = ClipboardEntry("2", "b", pinned = false, createdAtMs = now - ClipboardHistoryStore.TTL_MS - 1)
        val kept = ClipboardHistoryStore.pruneExpired(listOf(fresh, old), now)
        assertEquals(listOf(fresh), kept)
    }

    @Test
    fun pruneExpired_dropsPinnedPastPinnedTtl() {
        val now = 1_000_000L
        val pinnedFresh = ClipboardEntry(
            "3",
            "c",
            pinned = true,
            createdAtMs = now - ClipboardHistoryStore.PINNED_TTL_MS + 1_000,
        )
        val pinnedOld = ClipboardEntry(
            "4",
            "d",
            pinned = true,
            createdAtMs = now - ClipboardHistoryStore.PINNED_TTL_MS - 1,
        )
        val kept = ClipboardHistoryStore.pruneExpired(listOf(pinnedFresh, pinnedOld), now)
        assertEquals(listOf(pinnedFresh), kept)
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
