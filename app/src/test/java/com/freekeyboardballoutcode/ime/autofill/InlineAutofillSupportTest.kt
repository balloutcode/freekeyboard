package com.freekeyboardballoutcode.ime.autofill

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InlineAutofillSupportTest {
    @Test
    fun maxSuggestionCount_isThree() {
        assertEquals(3, InlineAutofillSupport.MAX_SUGGESTION_COUNT)
    }

    @Test
    fun chipMinSizePx_scalesWithDensity() {
        val size = InlineAutofillSupport.chipMinSizePx(density = 2f)
        assertEquals((InlineAutofillSupport.CHIP_MIN_WIDTH_DP * 2f).toInt(), size.width)
        assertEquals((InlineAutofillSupport.CHIP_HEIGHT_DP * 2f).toInt(), size.height)
    }

    @Test
    fun chipMaxSizePx_usesTwoThirdsScreenWidth() {
        val screen = 1080
        val size = InlineAutofillSupport.chipMaxSizePx(screenWidthPx = screen, density = 1f)
        assertEquals((screen * InlineAutofillSupport.CHIP_MAX_WIDTH_SCREEN_FRACTION).toInt(), size.width)
        assertEquals(InlineAutofillSupport.CHIP_HEIGHT_DP.toInt(), size.height)
        assertTrue(size.width < screen)
    }
}
