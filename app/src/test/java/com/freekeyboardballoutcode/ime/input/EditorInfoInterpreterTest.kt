package com.freekeyboardballoutcode.ime.input

import android.text.InputType
import android.view.inputmethod.EditorInfo
import org.junit.Assert.assertEquals
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

    @Test
    fun prefersNumberPad_forNumberAndPhone() {
        assertTrue(
            interpreter.prefersNumberPad(
                EditorInfo().apply { inputType = InputType.TYPE_CLASS_NUMBER },
            ),
        )
        assertTrue(
            interpreter.prefersNumberPad(
                EditorInfo().apply { inputType = InputType.TYPE_CLASS_PHONE },
            ),
        )
        assertTrue(interpreter.isPhoneField(
            EditorInfo().apply { inputType = InputType.TYPE_CLASS_PHONE },
        ))
        assertFalse(
            interpreter.prefersNumberPad(
                EditorInfo().apply { inputType = InputType.TYPE_CLASS_TEXT },
            ),
        )
    }

    @Test
    fun numberPasswordAndFlags() {
        val pin = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        }
        assertTrue(interpreter.isNumberPassword(pin))
        assertFalse(interpreter.numberAllowsDecimal(pin))

        val decimal = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        assertTrue(interpreter.numberAllowsDecimal(decimal))
        assertFalse(interpreter.isNumberPassword(decimal))

        val signed = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
        }
        assertTrue(interpreter.numberAllowsSigned(signed))
    }

    @Test
    fun prefersNewlineOnEnter_multilineUnspecified() {
        val multiline = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            imeOptions = EditorInfo.IME_ACTION_UNSPECIFIED
        }
        assertTrue(interpreter.prefersNewlineOnEnter(multiline))
        assertEquals(EnterKeyStyle.ENTER, interpreter.enterKeyStyle(multiline))

        val singleLineDone = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT
            imeOptions = EditorInfo.IME_ACTION_UNSPECIFIED
        }
        assertFalse(interpreter.prefersNewlineOnEnter(singleLineDone))
        assertEquals(EnterKeyStyle.DONE, interpreter.enterKeyStyle(singleLineDone))

        val multilineSend = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            imeOptions = EditorInfo.IME_ACTION_SEND
        }
        assertFalse(interpreter.prefersNewlineOnEnter(multilineSend))
        assertEquals(EnterKeyStyle.SEND, interpreter.enterKeyStyle(multilineSend))
    }

    @Test
    fun isIncognito_readsNoPersonalizedLearningFlag() {
        assertTrue(
            interpreter.isIncognito(
                EditorInfo().apply {
                    inputType = InputType.TYPE_CLASS_TEXT
                    imeOptions = EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING
                },
            ),
        )
        assertFalse(
            interpreter.isIncognito(
                EditorInfo().apply { inputType = InputType.TYPE_CLASS_TEXT },
            ),
        )
    }
}
