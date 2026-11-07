package com.freekeyboardballoutcode.ime.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class EmojiCategory(
    val title: String,
    val emoji: List<String>,
)

private val categories = listOf(
    EmojiCategory("Недавние", emptyList()),
    EmojiCategory(
        "Смайлы",
        listOf(
            "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "😊", "😇",
            "🙂", "😉", "😍", "🥰", "😘", "😗", "😋", "😜", "🤔", "😎",
            "😢", "😭", "😤", "😠", "🤯", "😴", "😷", "🤒", "🤠", "🥳",
        ),
    ),
    EmojiCategory(
        "Жесты",
        listOf(
            "👍", "👎", "👏", "🙌", "🤝", "🙏", "💪", "✌️", "🤞", "🤟",
            "👌", "🤘", "👋", "🖐️", "✋", "👊", "✊", "🤛", "🤜", "🤲",
        ),
    ),
    EmojiCategory(
        "Сердца",
        listOf(
            "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔",
            "❣️", "💕", "💞", "💓", "💗", "💖", "💘", "💝",
        ),
    ),
    EmojiCategory(
        "Животные",
        listOf(
            "🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐨", "🐯",
            "🦁", "🐮", "🐷", "🐸", "🐵", "🐔", "🐧", "🐦", "🦄", "🐝",
        ),
    ),
    EmojiCategory(
        "Еда",
        listOf(
            "🍎", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🫐", "🍒",
            "🍕", "🍔", "🍟", "🌭", "🌮", "🍣", "🍩", "🍪", "☕", "🍵",
        ),
    ),
    EmojiCategory(
        "Предметы",
        listOf(
            "⌚️", "📱", "💻", "⌨️", "🖥️", "🖨️", "🖱️", "🕹️", "🗜️", "💾",
            "📷", "📹", "🎥", "📞", "☎️", "📺", "🧭", "⏰", "🔋", "💡",
        ),
    ),
)

@Composable
fun EmojiPanel(
    recent: List<String>,
    onEmoji: (String) -> Unit,
    onClose: () -> Unit,
    numberRow: Boolean = false,
    includeSuggestionStrip: Boolean = true,
    emojiFontScale: Float = 1f,
    modifier: Modifier = Modifier,
) {
    var selected by remember { mutableIntStateOf(if (recent.isNotEmpty()) 0 else 1) }
    var query by remember { mutableStateOf("") }
    val tabs = categories.mapIndexed { index, category ->
        if (index == 0) category.copy(emoji = recent.ifEmpty { listOf("🙂") }) else category
    }
    val landscape = LocalConfiguration.current.orientation ==
        android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val panelHeight = GboardMetrics.letterLayerContentHeight(
        landscape = landscape,
        numberRow = numberRow,
        includeSuggestionStrip = includeSuggestionStrip,
    )

    val visible = remember(selected, query, tabs) {
        val base = tabs[selected].emoji
        if (query.isBlank()) {
            base
        } else {
            val q = query.trim().lowercase()
            val all = tabs.drop(1).flatMap { it.emoji }.distinct()
            val titleHit = tabs.filter { it.title.lowercase().contains(q) }.flatMap { it.emoji }
            EmojiSearch.searchAll(all, q, titleHit).ifEmpty { base }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(panelHeight)
            .padding(horizontal = 4.dp),
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            singleLine = true,
            placeholder = { Text("Поиск эмодзи") },
        )
        if (query.isBlank()) {
            PrimaryScrollableTabRow(selectedTabIndex = selected, edgePadding = 8.dp) {
                tabs.forEachIndexed { index, category ->
                    Tab(
                        selected = selected == index,
                        onClick = { selected = index },
                        text = { Text(category.title) },
                    )
                }
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 40.dp),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            items(visible) { emoji ->
                Text(
                    text = emoji,
                    fontSize = (26 * emojiFontScale).sp,
                    modifier = Modifier
                        .clickable { onEmoji(emoji) }
                        .padding(6.dp),
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(
                onClick = onClose,
                modifier = Modifier.height(48.dp),
            ) {
                Text("ABC", fontSize = 16.sp)
            }
        }
    }
}
