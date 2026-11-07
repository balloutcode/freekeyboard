package com.freekeyboardballoutcode.ime.input

import android.text.InputType
import android.view.inputmethod.EditorInfo

enum class EnterKeyStyle {
    ENTER,
    SEND,
    SEARCH,
    DONE,
    GO,
    NEXT,
}

class EditorInfoInterpreter {
    fun requiresRawKeys(editorInfo: EditorInfo?): Boolean {
        if (editorInfo == null) return false
        return editorInfo.inputType == InputType.TYPE_NULL
    }

    fun editorAction(editorInfo: EditorInfo?): Int {
        if (editorInfo == null) return EditorInfo.IME_ACTION_NONE
        val action = editorInfo.imeOptions and EditorInfo.IME_MASK_ACTION
        return if (action == EditorInfo.IME_ACTION_NONE || action == EditorInfo.IME_ACTION_UNSPECIFIED) {
            EditorInfo.IME_ACTION_DONE
        } else {
            action
        }
    }

    /**
     * Multiline editors with NONE/UNSPECIFIED ime action should insert `\n`
     * (Gboard/Android default) instead of firing DONE.
     */
    fun prefersNewlineOnEnter(editorInfo: EditorInfo?): Boolean {
        if (editorInfo == null) return false
        val multilines = editorInfo.inputType and InputType.TYPE_TEXT_FLAG_MULTI_LINE != 0
        if (!multilines) return false
        val action = editorInfo.imeOptions and EditorInfo.IME_MASK_ACTION
        return action == EditorInfo.IME_ACTION_NONE || action == EditorInfo.IME_ACTION_UNSPECIFIED
    }

    fun enterKeyStyle(editorInfo: EditorInfo?): EnterKeyStyle {
        if (prefersNewlineOnEnter(editorInfo)) return EnterKeyStyle.ENTER
        return when (editorAction(editorInfo)) {
            EditorInfo.IME_ACTION_SEND -> EnterKeyStyle.SEND
            EditorInfo.IME_ACTION_SEARCH -> EnterKeyStyle.SEARCH
            EditorInfo.IME_ACTION_DONE -> EnterKeyStyle.DONE
            EditorInfo.IME_ACTION_GO -> EnterKeyStyle.GO
            EditorInfo.IME_ACTION_NEXT -> EnterKeyStyle.NEXT
            else -> EnterKeyStyle.ENTER
        }
    }

    fun isPassword(editorInfo: EditorInfo?): Boolean {
        if (editorInfo == null) return false
        val variation = editorInfo.inputType and InputType.TYPE_MASK_VARIATION
        val klass = editorInfo.inputType and InputType.TYPE_MASK_CLASS
        return klass == InputType.TYPE_CLASS_TEXT && (
            variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
                variation == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
            )
    }

    /** Number / phone / datetime fields prefer a digit pad (A16-006). */
    fun prefersNumberPad(editorInfo: EditorInfo?): Boolean {
        if (editorInfo == null) return false
        val klass = editorInfo.inputType and InputType.TYPE_MASK_CLASS
        return klass == InputType.TYPE_CLASS_NUMBER ||
            klass == InputType.TYPE_CLASS_PHONE ||
            klass == InputType.TYPE_CLASS_DATETIME
    }

    fun isPhoneField(editorInfo: EditorInfo?): Boolean {
        if (editorInfo == null) return false
        return (editorInfo.inputType and InputType.TYPE_MASK_CLASS) == InputType.TYPE_CLASS_PHONE
    }

    fun isNumberClass(editorInfo: EditorInfo?): Boolean {
        if (editorInfo == null) return false
        return (editorInfo.inputType and InputType.TYPE_MASK_CLASS) == InputType.TYPE_CLASS_NUMBER
    }

    /** PIN / OTP style: `numberPassword`. */
    fun isNumberPassword(editorInfo: EditorInfo?): Boolean {
        if (!isNumberClass(editorInfo)) return false
        val variation = editorInfo!!.inputType and InputType.TYPE_MASK_VARIATION
        return variation == InputType.TYPE_NUMBER_VARIATION_PASSWORD
    }

    fun numberAllowsDecimal(editorInfo: EditorInfo?): Boolean {
        if (!isNumberClass(editorInfo)) return false
        return editorInfo!!.inputType and InputType.TYPE_NUMBER_FLAG_DECIMAL != 0
    }

    fun numberAllowsSigned(editorInfo: EditorInfo?): Boolean {
        if (!isNumberClass(editorInfo)) return false
        return editorInfo!!.inputType and InputType.TYPE_NUMBER_FLAG_SIGNED != 0
    }

    fun isIncognito(editorInfo: EditorInfo?): Boolean =
        LearningPolicy.hasNoPersonalizedLearningFlag(editorInfo)

    /** Editors that ask the IME not to show a suggestion strip. */
    fun suppressSuggestions(editorInfo: EditorInfo?): Boolean {
        if (editorInfo == null) return false
        if (requiresRawKeys(editorInfo) || isPassword(editorInfo)) return true
        val flags = editorInfo.inputType
        val klass = flags and InputType.TYPE_MASK_CLASS
        if (klass != 0 && klass != InputType.TYPE_CLASS_TEXT) return true
        if (flags and InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS != 0) return true
        val variation = flags and InputType.TYPE_MASK_VARIATION
        return variation == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS ||
            variation == InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS ||
            variation == InputType.TYPE_TEXT_VARIATION_URI ||
            variation == InputType.TYPE_TEXT_VARIATION_FILTER ||
            variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
    }
}
