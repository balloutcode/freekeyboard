package com.freekeyboardballoutcode.data

import android.content.ClipDescription
import android.os.Build

/**
 * Detects clips marked sensitive by password managers / the platform (API 33+).
 */
object ClipboardSensitivity {
    fun isSensitive(description: ClipDescription?): Boolean {
        if (description == null) return false
        return isMarkedSensitive(
            apiLevel = Build.VERSION.SDK_INT,
            extrasSensitive = description.extras
                ?.getBoolean(ClipDescription.EXTRA_IS_SENSITIVE, false),
        )
    }

    /** Pure helper for unit tests (no Android ClipDescription required). */
    fun isMarkedSensitive(apiLevel: Int, extrasSensitive: Boolean?): Boolean {
        if (apiLevel < Build.VERSION_CODES.TIRAMISU) return false
        return extrasSensitive == true
    }
}
