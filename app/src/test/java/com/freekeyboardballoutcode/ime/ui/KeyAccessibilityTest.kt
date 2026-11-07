package com.freekeyboardballoutcode.ime.ui

import com.freekeyboardballoutcode.data.ShiftMode
import com.freekeyboardballoutcode.ime.input.EnterKeyStyle
import com.freekeyboardballoutcode.ime.keyboard.KeyAction
import com.freekeyboardballoutcode.ime.keyboard.KeyDefinition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** A20-001/A20-004: special keys get a readable description instead of a raw glyph. */
class KeyAccessibilityTest {
    private fun key(action: KeyAction, label: String = "x") =
        KeyDefinition(code = "k", label = label, action = action)

    @Test
    fun noop_isHiddenFromAccessibility() {
        assertNull(
            KeyAccessibility.describe(
                key(KeyAction.NOOP),
                ShiftMode.OFF,
                EnterKeyStyle.ENTER,
                "",
            ),
        )
    }

    @Test
    fun shift_describesCurrentMode() {
        assertEquals(
            "Shift",
            KeyAccessibility.describe(key(KeyAction.SHIFT), ShiftMode.OFF, EnterKeyStyle.ENTER, "⇧"),
        )
        assertEquals(
            "Caps Lock включён",
            KeyAccessibility.describe(key(KeyAction.SHIFT), ShiftMode.CAPS, EnterKeyStyle.ENTER, "⇧"),
        )
    }

    @Test
    fun backspace_isDescribedNotGlyph() {
        assertEquals(
            "Удалить",
            KeyAccessibility.describe(key(KeyAction.BACKSPACE), ShiftMode.OFF, EnterKeyStyle.ENTER, "⌫"),
        )
    }

    @Test
    fun enter_followsEnterKeyStyle() {
        assertEquals(
            "Отправить",
            KeyAccessibility.describe(key(KeyAction.ENTER), ShiftMode.OFF, EnterKeyStyle.SEND, "↵"),
        )
        assertEquals(
            "Новая строка",
            KeyAccessibility.describe(key(KeyAction.ENTER), ShiftMode.OFF, EnterKeyStyle.ENTER, "↵"),
        )
    }

    @Test
    fun space_includesLanguageLabel() {
        assertEquals(
            "Пробел, текущий язык: Русский",
            KeyAccessibility.describe(key(KeyAction.SPACE), ShiftMode.OFF, EnterKeyStyle.ENTER, "Русский"),
        )
    }

    @Test
    fun letterKey_keepsVisibleLabel() {
        assertEquals(
            "й",
            KeyAccessibility.describe(key(KeyAction.COMMIT), ShiftMode.OFF, EnterKeyStyle.ENTER, "й"),
        )
    }
}
