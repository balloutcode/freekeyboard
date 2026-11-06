package com.freekeyboardballoutcode.ime.suggest

import com.freekeyboardballoutcode.data.InputLanguage
import kotlin.math.min

object SuggestionEngine {
    private val wordChar = Regex("""[\p{L}\p{N}'_-]+$""")
    private val lastWord = Regex("""[\p{L}\p{N}'_-]+(?=\s+$)""")

    fun currentPrefix(textBeforeCursor: CharSequence?): String {
        val before = textBeforeCursor?.toString().orEmpty()
        return wordChar.find(before)?.value.orEmpty()
    }

    fun previousWord(textBeforeCursor: CharSequence?): String? {
        val before = textBeforeCursor?.toString().orEmpty()
        return lastWord.find(before)?.value
    }

    fun suggestions(
        textBeforeCursor: CharSequence?,
        language: InputLanguage,
        limit: Int = 3,
        nextWordEnabled: Boolean = true,
        blockOffensive: Boolean = true,
        capitalizeNext: Boolean = false,
    ): List<String> {
        val prefix = currentPrefix(textBeforeCursor)
        val raw = if (prefix.isEmpty()) {
            if (!nextWordEnabled) emptyList()
            else nextWordSuggestions(textBeforeCursor, language, limit, capitalizeNext)
        } else {
            completionSuggestions(prefix, language, limit, blockOffensive)
        }
        return if (blockOffensive) {
            raw.filterNot { ProfanityLexicon.isProfane(it, language) }
        } else {
            raw
        }
    }

    private fun nextWordSuggestions(
        textBeforeCursor: CharSequence?,
        language: InputLanguage,
        limit: Int,
        capitalizeNext: Boolean,
    ): List<String> {
        val prev = previousWord(textBeforeCursor)
        return NextWordModel.nextWords(prev, language, limit).map { word ->
            if (capitalizeNext) word.replaceFirstChar { it.uppercaseChar() } else word
        }
    }

    private fun completionSuggestions(
        prefix: String,
        language: InputLanguage,
        limit: Int,
        blockOffensive: Boolean,
    ): List<String> {
        if (prefix.isEmpty()) return emptyList()
        val lower = prefix.lowercase()
        val words = SuggestionDictionary.words(language)
        val rankOffensive: (String) -> Int = { w ->
            if (blockOffensive && ProfanityLexicon.isProfane(w, language)) 1 else 0
        }

        val prefixHits = words.asSequence()
            .filter { it.startsWith(lower, ignoreCase = true) && !it.equals(prefix, ignoreCase = true) }
            .sortedWith(compareBy(rankOffensive).thenBy { it.length })
            .take(limit)
            .map { matchCase(prefix, it) }
            .toList()

        if (prefixHits.size >= limit) return prefixHits

        val fuzzy = words.asSequence()
            .filter { candidate ->
                val c = candidate.lowercase()
                if (c == lower) return@filter false
                val maxDist = when {
                    lower.length <= 3 -> 1
                    else -> 2
                }
                if (kotlin.math.abs(c.length - lower.length) > maxDist) return@filter false
                if (c.length <= 2) return@filter false
                editDistance(lower, c) <= maxDist
            }
            .sortedWith(
                compareBy(rankOffensive)
                    .thenBy { editDistance(lower, it.lowercase()) }
                    .thenBy { it.length },
            )
            .map { matchCase(prefix, it) }
            .filter { suggestion ->
                prefixHits.none { it.equals(suggestion, ignoreCase = true) }
            }
            .take(limit - prefixHits.size)
            .toList()

        return if (blockOffensive) {
            ProfanityLexicon.preferNeutral(prefixHits + fuzzy, language).take(limit)
        } else {
            (prefixHits + fuzzy).take(limit)
        }
    }

    private fun matchCase(prefix: String, word: String): String {
        if (prefix.isNotEmpty() && prefix.all { it.isUpperCase() }) return word.uppercase()
        if (prefix.firstOrNull()?.isUpperCase() == true) {
            return word.replaceFirstChar { it.uppercaseChar() }
        }
        return word
    }

    /** Classic Levenshtein; short strings only. */
    internal fun editDistance(a: String, b: String): Int {
        if (a == b) return 0
        if (a.isEmpty()) return b.length
        if (b.isEmpty()) return a.length
        val prev = IntArray(b.length + 1) { it }
        val curr = IntArray(b.length + 1)
        for (i in 1..a.length) {
            curr[0] = i
            for (j in 1..b.length) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                curr[j] = min(
                    min(curr[j - 1] + 1, prev[j] + 1),
                    prev[j - 1] + cost,
                )
            }
            for (j in prev.indices) prev[j] = curr[j]
        }
        return prev[b.length]
    }
}
