package com.freekeyboardballoutcode.ime.input

import kotlin.math.abs
import kotlin.math.sign

/**
 * Gboard-like spacebar horizontal swipe → move caret.
 * Tap still inserts space; long-press still opens the language picker.
 */
object SpaceCursorSwipe {
    /** Minimum travel before a gesture counts as cursor swipe (not tap / long-press). */
    const val THRESHOLD_DP = 24f

    /** Horizontal travel per one caret step after swipe engages. */
    const val STEP_DP = 12f

    /**
     * How many caret steps to emit for the current total drag, given how many
     * were already emitted in this gesture.
     */
    fun deltaSteps(
        totalDxPx: Float,
        stepPx: Float,
        thresholdPx: Float,
        alreadyEmittedSteps: Int,
    ): Int {
        if (stepPx <= 0f || abs(totalDxPx) < thresholdPx) return 0
        val beyond = abs(totalDxPx) - thresholdPx
        val totalSteps = (beyond / stepPx).toInt() + 1
        val signed = (sign(totalDxPx) * totalSteps).toInt()
        return signed - alreadyEmittedSteps
    }

    fun isSwipeEngaged(totalDxPx: Float, thresholdPx: Float): Boolean =
        abs(totalDxPx) >= thresholdPx
}
