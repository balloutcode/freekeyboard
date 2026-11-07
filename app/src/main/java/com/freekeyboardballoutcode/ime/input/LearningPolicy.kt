package com.freekeyboardballoutcode.ime.input

import android.text.InputType
import android.view.inputmethod.EditorInfo

/**
 * Explicit no-learn / privacy gates (A19-002 / A19-001).
 * freekeyboard has no personal learning store yet; this policy is the single gate
 * every future learn path MUST consult so password / incognito never write data.
 */
object LearningPolicy {
    fun hasNoPersonalizedLearningFlag(editorInfo: EditorInfo?): Boolean {
        if (editorInfo == null) return false
        return editorInfo.imeOptions and EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING != 0
    }

    /**
     * Whether any on-device learning / personalization may record text from this field.
     * False for passwords, no-learn flag, TYPE_NULL, email/URI, non-text classes.
     */
    fun mayLearnFromField(
        editorInfo: EditorInfo?,
        interpreter: EditorInfoInterpreter = EditorInfoInterpreter(),
    ): Boolean {
        if (editorInfo == null) return false
        if (hasNoPersonalizedLearningFlag(editorInfo)) return false
        if (interpreter.isPassword(editorInfo)) return false
        if (interpreter.requiresRawKeys(editorInfo)) return false
        if (interpreter.suppressSuggestions(editorInfo)) return false
        val klass = editorInfo.inputType and InputType.TYPE_MASK_CLASS
        if (klass != InputType.TYPE_CLASS_TEXT) return false
        return true
    }

    /**
     * Vacuous today: no learning store exists. Guarantees a no-op when learning is forbidden.
     * Call sites for future personal dictionaries MUST go through this.
     */
    fun recordTypedToken(
        token: String,
        editorInfo: EditorInfo?,
        store: LearningStore = NoOpLearningStore,
    ): Boolean {
        if (token.isBlank()) return false
        if (!mayLearnFromField(editorInfo)) return false
        store.record(token)
        return true
    }
}

/** Abstraction for a future on-device personal dictionary. */
fun interface LearningStore {
    fun record(token: String)
}

object NoOpLearningStore : LearningStore {
    override fun record(token: String) = Unit
}
