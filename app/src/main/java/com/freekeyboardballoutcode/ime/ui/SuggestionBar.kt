package com.freekeyboardballoutcode.ime.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.freekeyboardballoutcode.ui.theme.LocalKeyboardColors

/**
 * Gboard-style candidate strip: full-width 3 slots, measured ~52dp tall.
 * No Google toolbar icons (mic / apps / GIF) — candidates only.
 */
@Composable
fun SuggestionBar(
    suggestions: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalKeyboardColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(GboardMetrics.suggestionBarHeight)
            .background(colors.background)
            .padding(horizontal = GboardMetrics.suggestionHorizontalPad),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        val slots = (suggestions + List(3) { "" }).take(3)
        slots.forEachIndexed { index, word ->
            if (index > 0) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(GboardMetrics.suggestionDividerHeight)
                        .background(colors.onKey.copy(alpha = 0.16f)),
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .then(
                        if (word.isNotEmpty()) {
                            Modifier.clickable { onSelect(word) }
                        } else {
                            Modifier
                        },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (word.isNotEmpty()) {
                    Text(
                        text = word,
                        color = colors.onKey,
                        fontSize = GboardMetrics.suggestionFontSize,
                        fontWeight = if (index == 0) FontWeight.Medium else FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}
