package com.freekeyboardballoutcode.ime.keyboard.layouts

import com.freekeyboardballoutcode.ime.keyboard.KeyAction
import com.freekeyboardballoutcode.ime.keyboard.KeyDefinition
import com.freekeyboardballoutcode.ime.keyboard.KeyboardLayout
import com.freekeyboardballoutcode.ime.keyboard.KeyboardRow

object EnQwerty {
    /** Gboard EN long-press accents (US layout). Digit prepended for top-row keys. */
    private val accents: Map<String, List<String>> = mapOf(
        "a" to listOf("à", "á", "â", "ä", "æ", "ã", "å", "ā"),
        "c" to listOf("ç", "ć", "č"),
        "d" to listOf("ð"),
        "e" to listOf("è", "é", "ê", "ë", "ē", "ė", "ę", "ě"),
        "g" to listOf("ğ"),
        "i" to listOf("ì", "í", "î", "ï", "ī", "į", "ı"),
        "l" to listOf("ł"),
        "n" to listOf("ñ", "ń"),
        "o" to listOf("ò", "ó", "ô", "ö", "œ", "ø", "ō", "õ"),
        "s" to listOf("ß", "ś", "š", "ş"),
        "t" to listOf("þ"),
        "u" to listOf("ù", "ú", "û", "ü", "ū"),
        "y" to listOf("ÿ", "ý"),
        "z" to listOf("ž", "ź", "ż"),
    )

    private val topDigits = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")

    private fun letter(ch: String, digit: String? = null): KeyDefinition {
        val accent = accents[ch].orEmpty()
        val variants = buildList {
            if (digit != null) add(digit)
            addAll(accent)
        }
        return KeyDefinition(
            code = "en_$ch",
            label = ch,
            output = ch,
            variants = variants,
            hint = digit,
        )
    }

    val layout: KeyboardLayout = KeyboardLayout(
        id = "en_qwerty",
        rows = listOf(
            KeyboardRow(
                listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p").mapIndexed { i, ch ->
                    letter(ch, topDigits[i])
                },
            ),
            // Gboard: 9 home-row keys centered with ~0.5 key side insets.
            KeyboardRow(
                keys = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l").map { letter(it) },
                startInsetWeight = 0.5f,
                endInsetWeight = 0.5f,
            ),
            KeyboardRow(
                listOf(
                    KeyDefinition("shift", "⇧", action = KeyAction.SHIFT, weight = 1.5f, isSpecial = true),
                ) + listOf("z", "x", "c", "v", "b", "n", "m").map { letter(it) } + listOf(
                    KeyDefinition("backspace", "⌫", action = KeyAction.BACKSPACE, weight = 1.5f, isSpecial = true),
                ),
            ),
            // Gboard-like: ?123 | , | emoji | globe | space | . | enter
            KeyboardRow(
                listOf(
                    KeyDefinition("symbols", "?123", action = KeyAction.LAYER_SYMBOLS, weight = 1.25f, isSpecial = true),
                    KeyDefinition(
                        "comma", ",", output = ",", weight = 0.9f,
                        variants = listOf(",", "‚", "«", "»"),
                    ),
                    KeyDefinition("emoji", "☺", action = KeyAction.EMOJI, weight = 0.9f, isSpecial = true),
                    KeyDefinition("language", "🌐", action = KeyAction.LANGUAGE, weight = 0.9f, isSpecial = true),
                    KeyDefinition("space", "space", output = " ", action = KeyAction.SPACE, weight = 4.0f, isSpecial = true),
                    KeyDefinition(
                        "period", ".", output = ".", weight = 0.9f,
                        variants = listOf(".", "…", "·", "•"),
                    ),
                    KeyDefinition("enter", "↵", action = KeyAction.ENTER, weight = 1.25f, isSpecial = true),
                ),
            ),
        ),
    )

    fun shiftedLabel(key: KeyDefinition, caps: Boolean): String {
        if (key.action != KeyAction.COMMIT || key.output.length != 1) return key.label
        val ch = key.output.first()
        if (!ch.isLetter()) return key.label
        return if (caps) key.output.uppercase() else key.output
    }
}
