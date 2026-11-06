package com.freekeyboardballoutcode.ime.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GboardMetricsTest {
    @Test
    fun numberRow_addsOneKeyRowHeight() {
        val without = GboardMetrics.letterLayerContentHeightDp(
            landscape = false,
            numberRow = false,
            includeSuggestionStrip = true,
        )
        val with = GboardMetrics.letterLayerContentHeightDp(
            landscape = false,
            numberRow = true,
            includeSuggestionStrip = true,
        )
        val expectedExtra =
            GboardMetrics.keyHeightPortrait.value + GboardMetrics.rowGapPortrait.value
        assertEquals(expectedExtra.toDouble(), (with - without).toDouble(), 0.01)
    }

    @Test
    fun landscape_usesShorterKeys() {
        val portrait = GboardMetrics.letterLayerContentHeightDp(
            landscape = false,
            numberRow = false,
            includeSuggestionStrip = false,
        )
        val landscape = GboardMetrics.letterLayerContentHeightDp(
            landscape = true,
            numberRow = false,
            includeSuggestionStrip = false,
        )
        assertTrue(landscape < portrait)
    }
}
