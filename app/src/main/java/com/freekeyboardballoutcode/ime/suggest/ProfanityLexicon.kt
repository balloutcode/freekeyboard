package com.freekeyboardballoutcode.ime.suggest

import com.freekeyboardballoutcode.data.InputLanguage

/** Marks dictionary/next-word candidates that should rank below neutral words. */
object ProfanityLexicon {
    private val en = setOf(
        "fuck", "fucking", "fucked", "fucker", "fuckers", "motherfucker", "motherfuckers",
        "fuckin", "fck", "fuk", "clusterfuck", "shitstorm",
        "shit", "shitty", "bullshit", "horseshit",
        "ass", "asses", "asshole", "assholes", "dumbass", "badass",
        "bitch", "bitches", "bitchy",
        "damn", "damned", "dammit", "goddamn",
        "crap", "crappy",
        "hell", "bloody",
        "bastard", "bastards",
        "dick", "dicks", "dickhead",
        "cock", "cocks", "cocksucker",
        "pussy", "pussies",
        "cunt", "cunts",
        "piss", "pissed", "pissing",
        "slut", "sluts", "whore", "whores",
        "fag", "faggot", "faggots",
        "nigger", "nigga",
        "retard", "retarded",
        "wanker", "tosser", "bollocks", "bugger", "arse", "arsehole",
        "twat", "prick", "douche", "douchebag", "jackass", "dipshit", "shithead",
    )

    private val ru = setOf(
        "блядь", "блять", "бля", "блядский", "блядская", "блядство", "блядина", "бляди",
        "хуй", "хуя", "хуе", "хую", "хуём", "хуем", "хуи", "хуёв", "хуев",
        "хуйня", "хуевый", "хуевая", "хуево", "хуесос", "хуесосы", "хуйло",
        "нахуй", "нахуя", "похуй", "нихуя", "охуеть", "охуел", "охуела", "охуенно", "охуенный",
        "пизда", "пизде", "пизду", "пизды", "пиздец", "пиздёж", "пиздеж",
        "пиздить", "пиздит", "пиздишь", "пиздатый", "пиздато", "распиздяй", "опизденеть",
        "ебать", "ебал", "ебала", "ебали", "ебёт", "ебет", "ебут", "еби", "ебись",
        "ёб", "еб", "ёбаный", "ебаный", "ёбаная", "ебаная", "ёбаное", "ебаное",
        "заебал", "заебала", "заебали", "заебись", "заебать", "проебал", "проебать",
        "уебок", "уёбок", "уебище", "уёбище", "еблан", "ебланы", "ебанутый", "ебанутая",
        "мудак", "мудаки", "мудила", "мудило", "мудачьё",
        "сука", "суки", "сукин", "сучара", "сучка",
        "говно", "говнюк", "говноед", "дерьмо", "срать", "срал", "насрать", "посрать",
        "жопа", "жопы", "жопу", "задница",
        "залупа", "залупы", "манда", "манды",
        "пидор", "пидора", "пидоры", "пидорас", "пидорасы", "педик", "педрила",
        "шлюха", "шлюхи", "гандон", "гандоны", "конч", "конченый",
        "долбоёб", "долбоеб", "долбоёбы",
        "хер", "хрен", "нахрен", "похер", "херовый", "ёбнуть", "ебнуть", "выеб",
    )

    fun isProfane(word: String, language: InputLanguage): Boolean {
        val key = word.lowercase().trim().trimEnd('!', '.', ',', '?')
        return when (language) {
            InputLanguage.EN -> key in en
            InputLanguage.RU -> key in ru
        }
    }

    fun preferNeutral(words: List<String>, language: InputLanguage): List<String> =
        words.sortedBy { if (isProfane(it, language)) 1 else 0 }
}
