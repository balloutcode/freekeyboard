package com.freekeyboardballoutcode.ime.keyboard.layouts

import com.freekeyboardballoutcode.ime.keyboard.KeyAction
import com.freekeyboardballoutcode.ime.keyboard.KeyDefinition
import com.freekeyboardballoutcode.ime.keyboard.KeyboardLayout
import com.freekeyboardballoutcode.ime.keyboard.KeyboardRow

object RuJcuiken {
    /** Gboard RU: ё under е, ъ under ь; digits on first 10 top-row keys. */
    private val accents: Map<String, List<String>> = mapOf(
        "е" to listOf("ё"),
        "ь" to listOf("ъ"),
        "и" to listOf("і", "ї"),
        "г" to listOf("ґ"),
        "э" to listOf("є"),
        "у" to listOf("ў"),
    )

    private val topDigits = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")

    private fun letter(ch: String, digit: String? = null): KeyDefinition {
        val accent = accents[ch].orEmpty()
        val variants = buildList {
            if (digit != null) add(digit)
            addAll(accent)
        }
        return KeyDefinition(
            code = "ru_$ch",
            label = ch,
            output = ch,
            variants = variants,
            hint = digit,
        )
    }

    val layout: KeyboardLayout = KeyboardLayout(
        id = "ru_jcuiken",
        rows = listOf(
            KeyboardRow(
                listOf("й", "ц", "у", "к", "е", "н", "г", "ш", "щ", "з", "х").mapIndexed { i, ch ->
                    letter(ch, topDigits.getOrNull(i))
                },
            ),
            KeyboardRow(
                listOf("ф", "ы", "в", "а", "п", "р", "о", "л", "д", "ж", "э").map { letter(it) },
            ),
            // Weight 1+9+1=11 to match RU rows 1–2 (11 letter keys).
            KeyboardRow(
                listOf(
                    KeyDefinition("shift", "⇧", action = KeyAction.SHIFT, weight = 1f, isSpecial = true),
                ) + listOf("я", "ч", "с", "м", "и", "т", "ь", "б", "ю").map { letter(it) } + listOf(
                    KeyDefinition("backspace", "⌫", action = KeyAction.BACKSPACE, weight = 1f, isSpecial = true),
                ),
            ),
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
}
