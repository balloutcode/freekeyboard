package com.freekeyboardballoutcode.ime.ui

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Layout constants measured from Gboard on a 720×1280 @ 320dpi emulator
 * (density 2: 1dp = 2px). Google-only chrome (mic, apps menu, GIF, etc.) omitted.
 *
 * Gboard reference (portrait, no number row):
 * - Suggestion / toolbar strip: ~104px → 52dp
 * - Letter key face: ~81px → 40.5dp (bottom row ~83px)
 * - Row gap: ~20px → 10dp
 * - Key gap: ~9–10px → 5dp
 * - Side inset: ~9px → 4.5dp
 * - Screen bottom → spacebar bottom: ~108–110px → 54dp
 *   (IME nav strip + empty chrome under the last key row)
 */
object GboardMetrics {
    val suggestionBarHeight = 52.dp
    val suggestionFontSize = 18.sp
    val suggestionDividerHeight = 28.dp
    val suggestionHorizontalPad = 4.dp

    val keyHeightPortrait = 40.5.dp
    val keyHeightLandscape = 36.dp
    val rowGapPortrait = 10.dp
    val rowGapLandscape = 6.dp
    val keyGapPortrait = 5.dp
    val keyGapLandscape = 4.dp
    val sidePadPortrait = 4.dp
    val sidePadLandscape = 8.dp
    /** Tight to Gboard: toolbar sits directly above the first key row. */
    val gridVerticalPad = 1.dp
    /**
     * Padding under the last key row so the gap from the physical screen edge
     * to the spacebar matches Gboard (~54dp / 108px @ 320dpi).
     */
    const val IME_NAV_STRIP_DP = 54f
    const val IME_NAV_STRIP_MIN_PX = 108

    /** Letter/symbol layers always have 4 key rows; number row adds a 5th. */
    const val LETTER_KEY_ROWS = 4

    val letterCornerRadius = 8.dp
    val letterFontSize = 20.sp
    val wideLabelFontSize = 14.sp
    val keyIconSize = 22.dp
    val keyIconSizeSpecial = 24.dp

    /** Material You circular key-press popup (measured from Gboard). */
    val pressPopupDiameter = 52.dp
    val pressPopupGap = 6.dp
    val pressPopupFontSize = 24.sp

    /**
     * Content height of the letter layer (optional suggestion strip + key grid),
     * matching [KeyGrid] metrics for portrait/landscape and number row.
     */
    fun letterLayerContentHeight(
        landscape: Boolean,
        numberRow: Boolean,
        includeSuggestionStrip: Boolean,
    ): Dp {
        val keyH = if (landscape) keyHeightLandscape else keyHeightPortrait
        val gap = if (landscape) rowGapLandscape else rowGapPortrait
        val rows = LETTER_KEY_ROWS + if (numberRow) 1 else 0
        val gaps = (rows - 1).coerceAtLeast(0)
        var height = gridVerticalPad * 2 + keyH * rows + gap * gaps
        if (includeSuggestionStrip) height += suggestionBarHeight
        return height
    }

    /** Float dp total for first-frame inset estimates outside Compose. */
    fun letterLayerContentHeightDp(
        landscape: Boolean,
        numberRow: Boolean,
        includeSuggestionStrip: Boolean,
    ): Float = letterLayerContentHeight(landscape, numberRow, includeSuggestionStrip).value
}
