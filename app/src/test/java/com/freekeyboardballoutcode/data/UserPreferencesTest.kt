package com.freekeyboardballoutcode.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UserPreferencesTest {
    @Test
    fun cannotDisableLastLanguage() {
        val onlyEn = setOf(InputLanguage.EN)
        assertNull(LanguageRules.withLanguageEnabled(onlyEn, InputLanguage.EN, enable = false))
    }

    @Test
    fun canDisableWhenTwoEnabled() {
        val both = setOf(InputLanguage.EN, InputLanguage.RU)
        assertEquals(
            setOf(InputLanguage.RU),
            LanguageRules.withLanguageEnabled(both, InputLanguage.EN, enable = false),
        )
    }

    @Test
    fun withLanguageEnabledOnPrefsKeepsOne() {
        val prefs = UserPreferences(enabledLanguages = setOf(InputLanguage.EN))
        val next = prefs.withLanguageEnabled(InputLanguage.EN, enabled = false)
        assertEquals(setOf(InputLanguage.EN), next.enabledLanguages)
    }
}
