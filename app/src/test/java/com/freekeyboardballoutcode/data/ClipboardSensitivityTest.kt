package com.freekeyboardballoutcode.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ClipboardSensitivityTest {
    @Test
    fun preTiramisu_neverSensitive() {
        assertFalse(ClipboardSensitivity.isMarkedSensitive(apiLevel = 32, extrasSensitive = true))
        assertFalse(ClipboardSensitivity.isMarkedSensitive(apiLevel = 32, extrasSensitive = false))
    }

    @Test
    fun tiramisu_respectsExtrasFlag() {
        assertTrue(ClipboardSensitivity.isMarkedSensitive(apiLevel = 33, extrasSensitive = true))
        assertFalse(ClipboardSensitivity.isMarkedSensitive(apiLevel = 33, extrasSensitive = false))
        assertFalse(ClipboardSensitivity.isMarkedSensitive(apiLevel = 33, extrasSensitive = null))
    }
}
