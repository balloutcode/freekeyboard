package com.freekeyboardballoutcode.ime.keyboard.layouts

import com.freekeyboardballoutcode.ime.keyboard.KeyAction
import com.freekeyboardballoutcode.ime.keyboard.KeyDefinition
import com.freekeyboardballoutcode.ime.keyboard.KeyboardLayout
import com.freekeyboardballoutcode.ime.keyboard.KeyboardRow

/**
 * Digit-focused pads for TYPE_CLASS_NUMBER / PHONE / datetime (A16-006).
 * PIN / plain number: compact Gboard-like grid (no ABC / Space row).
 * Phone: dialer hints + * #.
 */
object NumberPad {
    private fun digit(d: String, hint: String? = null) =
        KeyDefinition(code = "num_$d", label = d, output = d, hint = hint)

    private fun special(code: String, label: String, output: String = label) =
        KeyDefinition(code = code, label = label, output = output, isSpecial = true)

    private fun noop(code: String = "num_noop") = KeyDefinition(
        code = code,
        label = " ",
        output = "",
        action = KeyAction.NOOP,
        isSpecial = true,
    )

    private val digitGridPhoneHints = listOf(
        KeyboardRow(listOf(digit("1"), digit("2", "ABC"), digit("3", "DEF"))),
        KeyboardRow(listOf(digit("4", "GHI"), digit("5", "JKL"), digit("6", "MNO"))),
        KeyboardRow(listOf(digit("7", "PQRS"), digit("8", "TUV"), digit("9", "WXYZ"))),
    )

    private val digitGridPlain = listOf(
        KeyboardRow(listOf(digit("1"), digit("2"), digit("3"))),
        KeyboardRow(listOf(digit("4"), digit("5"), digit("6"))),
        KeyboardRow(listOf(digit("7"), digit("8"), digit("9"))),
    )

    /**
     * Compact number / PIN pad: 3×3 digits, extras/0/BS, Enter row.
     * No ABC, Space, or comma — digit entry only.
     */
    fun numberLayout(
        allowDecimal: Boolean = false,
        allowSigned: Boolean = false,
        pinStyle: Boolean = false,
    ): KeyboardLayout {
        val id = when {
            pinStyle -> "number_pad_pin"
            allowDecimal || allowSigned -> "number_pad_flags"
            else -> "number_pad"
        }
        // Bottom-left of 0-row: decimal if only decimal; sign if only signed; decimal if both
        // (sign moves to the Enter row when both flags are set).
        val zeroRowLeft = when {
            allowDecimal -> special("num_dot", ".", ".")
            allowSigned -> special("num_sign", "−", "-")
            else -> noop()
        }
        val enterRowMid = when {
            allowDecimal && allowSigned -> special("num_sign", "−", "-")
            else -> noop("num_pad")
        }
        return KeyboardLayout(
            id = id,
            rows = digitGridPlain + listOf(
                KeyboardRow(
                    listOf(
                        zeroRowLeft,
                        digit("0"),
                        KeyDefinition(
                            "backspace", "⌫", action = KeyAction.BACKSPACE,
                            weight = 1f, isSpecial = true,
                        ),
                    ),
                ),
                KeyboardRow(
                    listOf(
                        KeyDefinition(
                            "symbols", "?123", action = KeyAction.LAYER_SYMBOLS,
                            weight = 1.25f, isSpecial = true,
                        ),
                        enterRowMid,
                        KeyDefinition(
                            "enter", "↵", action = KeyAction.ENTER,
                            weight = 1.25f, isSpecial = true,
                        ),
                    ),
                ),
            ),
        )
    }

    /** Phone dial pad with letter hints and * #. */
    val phoneLayout: KeyboardLayout = KeyboardLayout(
        id = "phone_pad",
        rows = digitGridPhoneHints + listOf(
            KeyboardRow(
                listOf(
                    special("num_star", "*", "*"),
                    digit("0", "+"),
                    special("num_hash", "#", "#"),
                ),
            ),
            KeyboardRow(
                listOf(
                    KeyDefinition(
                        "symbols", "?123", action = KeyAction.LAYER_SYMBOLS,
                        weight = 1.25f, isSpecial = true,
                    ),
                    KeyDefinition(
                        "backspace", "⌫", action = KeyAction.BACKSPACE,
                        weight = 1.5f, isSpecial = true,
                    ),
                    KeyDefinition(
                        "enter", "↵", action = KeyAction.ENTER,
                        weight = 1.25f, isSpecial = true,
                    ),
                ),
            ),
        ),
    )

    /** Default compact number pad (tests / fallback). */
    val layout: KeyboardLayout = numberLayout()
}
