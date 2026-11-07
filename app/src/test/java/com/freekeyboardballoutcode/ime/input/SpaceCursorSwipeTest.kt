package com.freekeyboardballoutcode.ime.input

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SpaceCursorSwipeTest {
    @Test
    fun belowThreshold_noSteps() {
        assertFalse(SpaceCursorSwipe.isSwipeEngaged(10f, thresholdPx = 24f))
        assertEquals(
            0,
            SpaceCursorSwipe.deltaSteps(
                totalDxPx = 10f,
                stepPx = 12f,
                thresholdPx = 24f,
                alreadyEmittedSteps = 0,
            ),
        )
    }

    @Test
    fun rightSwipe_emitsPositiveSteps() {
        assertTrue(SpaceCursorSwipe.isSwipeEngaged(30f, thresholdPx = 24f))
        assertEquals(
            1,
            SpaceCursorSwipe.deltaSteps(
                totalDxPx = 30f,
                stepPx = 12f,
                thresholdPx = 24f,
                alreadyEmittedSteps = 0,
            ),
        )
        assertEquals(
            1,
            SpaceCursorSwipe.deltaSteps(
                totalDxPx = 42f,
                stepPx = 12f,
                thresholdPx = 24f,
                alreadyEmittedSteps = 1,
            ),
        )
    }

    @Test
    fun leftSwipe_emitsNegativeSteps() {
        assertEquals(
            -1,
            SpaceCursorSwipe.deltaSteps(
                totalDxPx = -30f,
                stepPx = 12f,
                thresholdPx = 24f,
                alreadyEmittedSteps = 0,
            ),
        )
    }
}
