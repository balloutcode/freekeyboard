package com.freekeyboardballoutcode.ime.keyboard.layouts

import com.freekeyboardballoutcode.ime.keyboard.KeyAction
import com.freekeyboardballoutcode.ime.keyboard.KeyDefinition
import com.freekeyboardballoutcode.ime.keyboard.KeyboardLayout
import com.freekeyboardballoutcode.ime.keyboard.KeyboardRow

object RuJcuiken {
    /**
     * Gboard-like RU long-press variants: ё/ъ plus Ukrainian/Belarusian neighbors
     * and acute-marked vowels commonly offered on RU layouts.
     */
    private val accents: Map<String, List<String>> = mapOf(
        "а" to listOf("а́", "ӕ"),
        "е" to listOf("ё", "е́", "є"),
        "о" to listOf("о́"),
        "у" to listOf("ў", "у́"),
        "и" to listOf("і", "ї", "и́", "ы́"),
        "ы" to listOf("ы́"),
        "э" to listOf("є", "э́"),
        "ю" to listOf("ю́"),
        "я" to listOf("я́"),
        "г" to listOf("ґ"),
        "к" to listOf("қ"),
        "х" to listOf("ҳ"),
        "ж" to listOf("җ"),
        "ш" to listOf("щ"),
        "ь" to listOf("ъ"),
        "й" to listOf("ј"),
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
                        variants = listOf(",", "‚", "«", "»", "⚙"),
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
