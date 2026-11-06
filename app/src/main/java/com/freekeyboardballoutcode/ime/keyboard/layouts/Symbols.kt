package com.freekeyboardballoutcode.ime.keyboard.layouts

import com.freekeyboardballoutcode.ime.keyboard.KeyAction
import com.freekeyboardballoutcode.ime.keyboard.KeyDefinition
import com.freekeyboardballoutcode.ime.keyboard.KeyboardLayout
import com.freekeyboardballoutcode.ime.keyboard.KeyboardRow

object Symbols {
    private fun sym(ch: String, variants: List<String> = emptyList()) =
        KeyDefinition(code = "sym_$ch", label = ch, output = ch, variants = variants)

    private val symbolVariants: Map<String, List<String>> = mapOf(
        "$" to listOf("$", "€", "£", "¥", "₹", "¢"),
        "-" to listOf("-", "–", "—", "_", "•"),
        "/" to listOf("/", "\\"),
        "*" to listOf("*", "†", "‡", "★"),
        "\"" to listOf("\"", "“", "”", "„"),
        "'" to listOf("'", "‘", "’", "‚"),
        "!" to listOf("!", "¡"),
        "?" to listOf("?", "¿"),
        "." to listOf(".", "…", "·", "•"),
        "," to listOf(",", "‚"),
        "0" to listOf("0", "°"),
        "1" to listOf("1", "¹", "½", "⅓", "¼"),
        "2" to listOf("2", "²", "⅔"),
        "3" to listOf("3", "³", "¾"),
    )

    val layout: KeyboardLayout = KeyboardLayout(
        id = "symbols",
        rows = listOf(
            KeyboardRow((1..9).map { sym(it.toString(), symbolVariants[it.toString()].orEmpty()) } +
                listOf(sym("0", symbolVariants["0"].orEmpty()))),
            KeyboardRow(
                listOf("@", "#", "$", "%", "&", "-", "+", "(", ")").map {
                    sym(it, symbolVariants[it].orEmpty())
                } + listOf(sym("/", symbolVariants["/"].orEmpty())),
            ),
            // Weight 1.5+7+1.5=10 to match digit rows (comma lives on the bottom row).
            KeyboardRow(
                listOf(
                    KeyDefinition("shift", "=\\<", action = KeyAction.SHIFT, weight = 1.5f, isSpecial = true),
                ) + listOf("*", "\"", "'", ":", ";", "!", "?").map {
                    sym(it, symbolVariants[it].orEmpty())
                } + listOf(
                    KeyDefinition("backspace", "⌫", action = KeyAction.BACKSPACE, weight = 1.5f, isSpecial = true),
                ),
            ),
            KeyboardRow(
                listOf(
                    KeyDefinition("letters", "ABC", action = KeyAction.LAYER_LETTERS, weight = 1.25f, isSpecial = true),
                    KeyDefinition("comma", ",", output = ",", weight = 0.9f, variants = symbolVariants[","].orEmpty()),
                    KeyDefinition("emoji", "☺", action = KeyAction.EMOJI, weight = 0.9f, isSpecial = true),
                    KeyDefinition("language", "🌐", action = KeyAction.LANGUAGE, weight = 0.9f, isSpecial = true),
                    KeyDefinition("space", "space", output = " ", action = KeyAction.SPACE, weight = 4.0f, isSpecial = true),
                    KeyDefinition("period", ".", output = ".", weight = 0.9f, variants = symbolVariants["."].orEmpty()),
                    KeyDefinition("enter", "↵", action = KeyAction.ENTER, weight = 1.25f, isSpecial = true),
                ),
            ),
        ),
    )

    val altLayout: KeyboardLayout = KeyboardLayout(
        id = "symbols_alt",
        rows = listOf(
            KeyboardRow(listOf("~", "`", "|", "•", "√", "π", "÷", "×", "¶", "△").map { sym(it) }),
            KeyboardRow(listOf("£", "€", "¥", "^", "°", "=", "{", "}", "\\", "©").map { sym(it) }),
            // Weight 1.5+7+1.5=10 to match rows 1–2.
            KeyboardRow(
                listOf(
                    KeyDefinition("shift", "?123", action = KeyAction.SHIFT, weight = 1.5f, isSpecial = true),
                ) + listOf("™", "®", "℅", "[", "]", "¡", "¿").map { KeyDefinition("sym_$it", it, it) } +
                    listOf(KeyDefinition("backspace", "⌫", action = KeyAction.BACKSPACE, weight = 1.5f, isSpecial = true)),
            ),
            KeyboardRow(
                listOf(
                    KeyDefinition("letters", "ABC", action = KeyAction.LAYER_LETTERS, weight = 1.25f, isSpecial = true),
                    KeyDefinition("comma", ",", output = ",", weight = 0.9f),
                    KeyDefinition("emoji", "☺", action = KeyAction.EMOJI, weight = 0.9f, isSpecial = true),
                    KeyDefinition("language", "🌐", action = KeyAction.LANGUAGE, weight = 0.9f, isSpecial = true),
                    KeyDefinition("space", "space", output = " ", action = KeyAction.SPACE, weight = 4.0f, isSpecial = true),
                    KeyDefinition("period", ".", output = ".", weight = 0.9f),
                    KeyDefinition("enter", "↵", action = KeyAction.ENTER, weight = 1.25f, isSpecial = true),
                ),
            ),
        ),
    )
}
