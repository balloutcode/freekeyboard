package com.freekeyboardballoutcode.ime.suggest

import com.freekeyboardballoutcode.data.InputLanguage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SuggestionEngineTest {
    @Test
    fun prefixFromTextBeforeCursor() {
        assertEquals("hel", SuggestionEngine.currentPrefix("say hel"))
        assertEquals("При", SuggestionEngine.currentPrefix("При"))
        assertEquals("", SuggestionEngine.currentPrefix("hello "))
    }

    @Test
    fun previousWordAfterSpace() {
        assertEquals("hello", SuggestionEngine.previousWord("hello "))
        assertEquals("привет", SuggestionEngine.previousWord("ну привет "))
        assertEquals(null, SuggestionEngine.previousWord("hel"))
    }

    @Test
    fun englishSuggestions() {
        val result = SuggestionEngine.suggestions("hel", InputLanguage.EN)
        assertTrue(result.any { it.equals("hello", ignoreCase = true) })
    }

    @Test
    fun russianSuggestions() {
        val result = SuggestionEngine.suggestions("при", InputLanguage.RU)
        assertTrue(result.any { it.startsWith("при", ignoreCase = true) })
    }

    @Test
    fun russianPrivPrefixSuggestsPrivet() {
        val result = SuggestionEngine.suggestions("прив", InputLanguage.RU)
        assertTrue(
            "Expected привет-like candidate for прив, got $result",
            result.any { it.startsWith("привет", ignoreCase = true) },
        )
    }

    @Test
    fun preservesCapitalization() {
        val result = SuggestionEngine.suggestions("Hel", InputLanguage.EN)
        assertTrue(result.first().startsWith("H"))
    }

    @Test
    fun nextWordAfterEnglish() {
        val result = SuggestionEngine.suggestions("thank ", InputLanguage.EN)
        assertTrue(result.any { it.equals("you", ignoreCase = true) })
    }

    @Test
    fun nextWordAfterRussian() {
        val result = SuggestionEngine.suggestions("привет ", InputLanguage.RU)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun emptyFieldShowsDefaults() {
        val result = SuggestionEngine.suggestions("", InputLanguage.EN)
        assertEquals(3, result.size)
    }

    @Test
    fun nextWordCapitalizesAtSentenceStart() {
        val result = SuggestionEngine.suggestions(
            textBeforeCursor = "",
            language = InputLanguage.EN,
            capitalizeNext = true,
        )
        assertTrue(result.isNotEmpty())
        assertTrue(result.all { it.first().isUpperCase() })
    }

    @Test
    fun fuzzyCorrectsEnglishTypoWhenOffensiveAllowed() {
        val result = SuggestionEngine.suggestions(
            "fukc",
            InputLanguage.EN,
            blockOffensive = false,
        )
        assertTrue(result.any { it.equals("fuck", ignoreCase = true) })
    }

    @Test
    fun fuzzyCorrectsRussianTypoWhenOffensiveAllowed() {
        val result = SuggestionEngine.suggestions(
            "блдяь",
            InputLanguage.RU,
            blockOffensive = false,
        )
        assertTrue(result.any { it.startsWith("бля", ignoreCase = true) })
    }

    @Test
    fun blockOffensiveHidesProfanitySuggestions() {
        val result = SuggestionEngine.suggestions(
            "fukc",
            InputLanguage.EN,
            blockOffensive = true,
        )
        assertTrue(result.none { it.equals("fuck", ignoreCase = true) })
    }

    @Test
    fun editDistanceBasics() {
        assertEquals(0, SuggestionEngine.editDistance("fuck", "fuck"))
        assertEquals(1, SuggestionEngine.editDistance("fuk", "fuck"))
        assertEquals(2, SuggestionEngine.editDistance("fukc", "fuck"))
    }

    @Test
    fun autocorrectFixesNearMiss() {
        // Single insertion: helllo → hello (length-penalized over other distance-1 neighbors).
        val hit = SuggestionEngine.autocorrectCandidate("helllo", InputLanguage.EN)
        assertEquals("hello", hit)
    }

    @Test
    fun autocorrectSkipsKnownWord() {
        assertEquals(null, SuggestionEngine.autocorrectCandidate("hello", InputLanguage.EN))
    }

    @Test
    fun personalDictionarySurfacesInSuggestions() {
        SuggestionEngine.setPersonalWords(listOf("freekeyboard"))
        try {
            val result = SuggestionEngine.suggestions("freek", InputLanguage.EN)
            assertTrue(result.any { it.equals("freekeyboard", ignoreCase = true) })
        } finally {
            SuggestionEngine.setPersonalWords(emptyList())
        }
    }
}
