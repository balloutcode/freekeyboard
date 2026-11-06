package com.freekeyboardballoutcode.ime.input

import android.text.InputType
import android.view.inputmethod.EditorInfo
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EditorInfoInterpreterTest {
    private val interpreter = EditorInfoInterpreter()

    @Test
    fun suppressSuggestions_typeNullAndPassword() {
        assertTrue(
            interpreter.suppressSuggestions(
                EditorInfo().apply { inputType = InputType.TYPE_NULL },
            ),
        )
        assertTrue(
            interpreter.suppressSuggestions(
                EditorInfo().apply {
                    inputType = InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_PASSWORD
                },
            ),
        )
    }

    @Test
    fun suppressSuggestions_emailUriNumber() {
        assertTrue(
            interpreter.suppressSuggestions(
                EditorInfo().apply {
                    inputType = InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                },
            ),
        )
        assertTrue(
            interpreter.suppressSuggestions(
                EditorInfo().apply {
                    inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI
                },
            ),
        )
        assertTrue(
            interpreter.suppressSuggestions(
                EditorInfo().apply { inputType = InputType.TYPE_CLASS_NUMBER },
            ),
        )
    }

    @Test
    fun suppressSuggestions_plainText_allows() {
        assertFalse(
            interpreter.suppressSuggestions(
                EditorInfo().apply {
                    inputType = InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_FLAG_MULTI_LINE
                },
            ),
        )
        assertFalse(interpreter.suppressSuggestions(null))
    }
}
