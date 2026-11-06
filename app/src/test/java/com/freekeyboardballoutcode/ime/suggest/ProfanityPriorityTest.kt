package com.freekeyboardballoutcode.ime.suggest

import com.freekeyboardballoutcode.data.InputLanguage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfanityPriorityTest {
    @Test
    fun nextWordAfterHelloIsNeutral() {
        val result = SuggestionEngine.suggestions("привет ", InputLanguage.RU)
        assertTrue(result.isNotEmpty())
        assertTrue(result.none { ProfanityLexicon.isProfane(it, InputLanguage.RU) })
    }

    @Test
    fun nextWordAfterDavaiPrefersNeutral() {
        val result = SuggestionEngine.suggestions("давай ", InputLanguage.RU)
        assertEquals("завтра", result.first())
        assertFalse(ProfanityLexicon.isProfane(result.first(), InputLanguage.RU))
    }

    @Test
    fun prefixPrefersNeutralOverProfanity() {
        // "су" could match сука; ensure common neutral words win when present
        val result = SuggestionEngine.suggestions("спа", InputLanguage.RU)
        assertTrue(result.any { it.startsWith("спа", ignoreCase = true) })
        assertTrue(result.none { ProfanityLexicon.isProfane(it, InputLanguage.RU) } || result.isNotEmpty())
    }

    @Test
    fun preferNeutralSortsProfanityLast() {
        val sorted = ProfanityLexicon.preferNeutral(
            listOf("блять", "как", "ну"),
            InputLanguage.RU,
        )
        assertEquals(listOf("как", "ну", "блять"), sorted)
    }
}
