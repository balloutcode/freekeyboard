package com.freekeyboardballoutcode.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.rejectedStore: DataStore<Preferences> by preferencesDataStore(name = "rejected_suggestions")

/** Local reject list for suggestion chips (A10-007). Offline only — no cloud learning. */
class RejectedSuggestionsStore(private val context: Context) {
    private val key = stringSetPreferencesKey("rejected")

    val rejected: Flow<Set<String>> = context.rejectedStore.data.map { prefs ->
        prefs[key]?.map { it.lowercase() }?.toSet().orEmpty()
    }

    suspend fun reject(word: String) {
        val normalized = word.trim().lowercase()
        if (normalized.isEmpty()) return
        context.rejectedStore.edit { prefs ->
            val next = (prefs[key].orEmpty() + normalized).take(MAX).toSet()
            prefs[key] = next
        }
    }

    companion object {
        const val MAX = 200
    }
}
