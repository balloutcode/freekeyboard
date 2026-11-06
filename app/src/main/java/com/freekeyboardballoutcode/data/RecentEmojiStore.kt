package com.freekeyboardballoutcode.data

object RecentEmojiStore {
    fun upsert(current: List<String>, emoji: String, maxItems: Int = UserPreferencesRepository.MAX_RECENT_EMOJI): List<String> {
        val next = current.toMutableList()
        next.remove(emoji)
        next.add(0, emoji)
        return next.take(maxItems)
    }
}
