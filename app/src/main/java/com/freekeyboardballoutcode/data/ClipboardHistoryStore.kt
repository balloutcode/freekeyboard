package com.freekeyboardballoutcode.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.clipboardStore: DataStore<Preferences> by preferencesDataStore(name = "clipboard_history")

data class ClipboardEntry(
    val id: String,
    val text: String,
    val pinned: Boolean,
    val createdAtMs: Long,
)

/**
 * Local-only clipboard history (A13). Unpinned entries expire after [TTL_MS].
 * No phone↔PC sync, no screenshots.
 */
class ClipboardHistoryStore(private val context: Context) {
    private val entriesKey = stringPreferencesKey("entries_json")

    val entries: Flow<List<ClipboardEntry>> = context.clipboardStore.data.map { prefs ->
        decode(prefs[entriesKey]).let { pruneExpired(it, System.currentTimeMillis()) }
    }

    suspend fun capture(text: String, nowMs: Long = System.currentTimeMillis()) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        context.clipboardStore.edit { prefs ->
            val current = pruneExpired(decode(prefs[entriesKey]), nowMs).toMutableList()
            current.removeAll { it.text == trimmed && !it.pinned }
            current.add(
                0,
                ClipboardEntry(
                    id = "${nowMs}_${trimmed.hashCode()}",
                    text = trimmed.take(MAX_CHARS),
                    pinned = false,
                    createdAtMs = nowMs,
                ),
            )
            while (current.size > MAX_ENTRIES) {
                val dropIdx = current.indexOfLast { !it.pinned }
                if (dropIdx < 0) break
                current.removeAt(dropIdx)
            }
            prefs[entriesKey] = encode(current)
        }
    }

    suspend fun setPinned(id: String, pinned: Boolean) {
        context.clipboardStore.edit { prefs ->
            val now = System.currentTimeMillis()
            val current = pruneExpired(decode(prefs[entriesKey]), now).map {
                if (it.id == id) it.copy(pinned = pinned) else it
            }
            prefs[entriesKey] = encode(current)
        }
    }

    suspend fun remove(id: String) {
        context.clipboardStore.edit { prefs ->
            val now = System.currentTimeMillis()
            val current = pruneExpired(decode(prefs[entriesKey]), now).filterNot { it.id == id }
            prefs[entriesKey] = encode(current)
        }
    }

    companion object {
        const val TTL_MS = 60L * 60L * 1000L
        const val MAX_ENTRIES = 25
        const val MAX_CHARS = 500

        fun pruneExpired(entries: List<ClipboardEntry>, nowMs: Long): List<ClipboardEntry> =
            entries.filter { it.pinned || nowMs - it.createdAtMs <= TTL_MS }

        /** Line-oriented codec (unit-testable without mocked org.json). */
        fun encode(entries: List<ClipboardEntry>): String =
            entries.joinToString("\n") { e ->
                listOf(
                    escape(e.id),
                    escape(e.text),
                    if (e.pinned) "1" else "0",
                    e.createdAtMs.toString(),
                ).joinToString("\t")
            }

        fun decode(raw: String?): List<ClipboardEntry> {
            if (raw.isNullOrBlank()) return emptyList()
            return raw.lineSequence()
                .mapNotNull { line ->
                    val parts = line.split('\t')
                    if (parts.size < 4) return@mapNotNull null
                    ClipboardEntry(
                        id = unescape(parts[0]),
                        text = unescape(parts[1]),
                        pinned = parts[2] == "1",
                        createdAtMs = parts[3].toLongOrNull() ?: 0L,
                    )
                }
                .toList()
        }

        private fun escape(value: String): String =
            value.replace("\\", "\\\\").replace("\t", "\\t").replace("\n", "\\n")

        private fun unescape(value: String): String =
            buildString(value.length) {
                var i = 0
                while (i < value.length) {
                    val c = value[i]
                    if (c == '\\' && i + 1 < value.length) {
                        when (value[i + 1]) {
                            '\\' -> append('\\')
                            't' -> append('\t')
                            'n' -> append('\n')
                            else -> append(value[i + 1])
                        }
                        i += 2
                    } else {
                        append(c)
                        i++
                    }
                }
            }
    }
}
