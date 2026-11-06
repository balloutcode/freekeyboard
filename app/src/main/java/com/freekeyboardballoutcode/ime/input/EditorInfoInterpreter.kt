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

    fun enterKeyStyle(editorInfo: EditorInfo?): EnterKeyStyle {
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
