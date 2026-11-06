package com.freekeyboardballoutcode.ime.keyboard

enum class KeyAction {
    COMMIT,
    /** Inert spacer (e.g. RU number-row pad); must not commit or consume shift. */
    NOOP,
    SHIFT,
    BACKSPACE,
    SPACE,
    ENTER,
    LAYER_SYMBOLS,
    LAYER_LETTERS,
    EMOJI,
    LANGUAGE,
}

data class KeyDefinition(
    val code: String,
    val label: String,
    val output: String = label,
    val variants: List<String> = emptyList(),
    /** Small top-right hint (e.g. digit on letter keys), Gboard-style. */
    val hint: String? = null,
    val action: KeyAction = KeyAction.COMMIT,
    val weight: Float = 1f,
    val isSpecial: Boolean = false,
)

data class KeyboardRow(
    val keys: List<KeyDefinition>,
    /** Gboard-style side inset for shorter rows (e.g. EN home row). */
    val startInsetWeight: Float = 0f,
    val endInsetWeight: Float = 0f,
)

data class KeyboardLayout(
    val id: String,
    val rows: List<KeyboardRow>,
)
