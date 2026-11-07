package com.freekeyboardballoutcode.ime.keyboard

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordNumberRowTest {
    @Test
    fun effectiveNumberRow_passwordPrefRequiresPasswordField() {
        assertFalse(
            KeyboardUiState(numberRow = false, numberRowForPasswords = true, isPasswordField = false)
                .effectiveNumberRow,
        )
        assertTrue(
            KeyboardUiState(numberRow = false, numberRowForPasswords = true, isPasswordField = true)
                .effectiveNumberRow,
        )
    }

    @Test
    fun globalNumberRow_alwaysEffective() {
        assertTrue(
            KeyboardUiState(numberRow = true, numberRowForPasswords = false, isPasswordField = false)
                .effectiveNumberRow,
        )
    }
}
