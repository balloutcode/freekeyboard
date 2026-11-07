package com.freekeyboardballoutcode.ime.input

import android.text.InputType
import android.view.inputmethod.EditorInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LearningPolicyTest {
    private val recording = mutableListOf<String>()
    private val store = LearningStore { recording += it }

    @Test
    fun password_neverLearns() {
        val info = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        assertFalse(LearningPolicy.mayLearnFromField(info))
        assertFalse(LearningPolicy.recordTypedToken("secret", info, store))
        assertTrue(recording.isEmpty())
    }

    @Test
    fun visiblePassword_neverLearns() {
        val info = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        }
        assertFalse(LearningPolicy.mayLearnFromField(info))
    }

    @Test
    fun noPersonalizedLearningFlag_blocks() {
        val info = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT
            imeOptions = EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING
        }
        assertTrue(LearningPolicy.hasNoPersonalizedLearningFlag(info))
        assertFalse(LearningPolicy.mayLearnFromField(info))
        assertFalse(LearningPolicy.recordTypedToken("hello", info, store))
        assertTrue(recording.isEmpty())
    }

    @Test
    fun plainText_mayLearnWhenStorePresent() {
        val info = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
        }
        assertTrue(LearningPolicy.mayLearnFromField(info))
        assertTrue(LearningPolicy.recordTypedToken("hello", info, store))
        assertEquals(listOf("hello"), recording)
    }
}
