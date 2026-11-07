package com.freekeyboardballoutcode.ime.keyboard

/** Applies Gboard-style layout preferences to a base layout. */
object LayoutOptions {
    fun apply(
        layout: KeyboardLayout,
        numberRow: Boolean,
        showEmojiKey: Boolean,
        longPressForSymbols: Boolean,
        showLanguageKey: Boolean = true,
    ): KeyboardLayout {
        var rows = layout.rows.map { row ->
            KeyboardRow(
                keys = row.keys.map { key ->
                    transformKey(key, numberRow, longPressForSymbols)
                }.let { keys ->
                    var next = keys
                    if (!showEmojiKey) next = next.filterNot { it.action == KeyAction.EMOJI }
                    if (!showLanguageKey) next = next.filterNot { it.action == KeyAction.LANGUAGE }
                    next
                },
                startInsetWeight = row.startInsetWeight,
                endInsetWeight = row.endInsetWeight,
            )
        }
        if (numberRow &&
            layout.id != "symbols" &&
            layout.id != "symbols_alt" &&
            layout.id != "number_pad" &&
            layout.id != "phone_pad"
        ) {
            // Match top letter-row key count (EN=10, RU=11) so columns line up.
            val topLetterCount = rows.firstOrNull()?.keys?.size ?: 10
            val digitLabels = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
            val digits = buildList {
                for (i in 0 until topLetterCount) {
                    if (i < digitLabels.size) {
                        val d = digitLabels[i]
                        add(KeyDefinition(code = "num_$d", label = d, output = d))
                    } else {
                        // Extra slot for 11-key RU top row — inert pad (no commit / no shift consume).
                        add(
                            KeyDefinition(
                                code = "num_pad",
                                label = "",
                                output = "",
                                action = KeyAction.NOOP,
                                isSpecial = true,
                            ),
                        )
                    }
                }
            }
            rows = listOf(KeyboardRow(digits)) + rows
        }
        return layout.copy(id = layout.id, rows = rows)
    }

    private fun transformKey(
        key: KeyDefinition,
        numberRow: Boolean,
        longPressForSymbols: Boolean,
    ): KeyDefinition {
        var variants = key.variants
        var hint = key.hint
        if (numberRow && hint != null && hint.all { it.isDigit() }) {
            // Digits live on the number row — clear letter-key digit hints/variants.
            hint = null
            variants = variants.filterNot { it.length == 1 && it.first().isDigit() }
        }
        if (!longPressForSymbols) {
            variants = emptyList()
            hint = null
        }
        return key.copy(variants = variants, hint = hint)
    }
}
