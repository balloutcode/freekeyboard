package com.freekeyboardballoutcode.ime.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freekeyboardballoutcode.data.ClipboardEntry
import com.freekeyboardballoutcode.ui.theme.LocalKeyboardColors

@Composable
fun ClipboardPanel(
    entries: List<ClipboardEntry>,
    onPaste: (String) -> Unit,
    onPin: (ClipboardEntry) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalKeyboardColors.current
    val pinned = entries.filter { it.pinned }
    val recent = entries.filterNot { it.pinned }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = GboardMetrics.letterLayerContentHeight(false, false, true))
            .background(colors.background)
            .padding(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("Буфер обмена", color = colors.onKey, fontWeight = FontWeight.Medium, fontSize = 16.sp)
            Text(
                "ABC",
                color = colors.onKey,
                modifier = Modifier
                    .clickable(onClick = onClose)
                    .padding(8.dp),
            )
        }
        if (entries.isEmpty()) {
            Text(
                "Скопированный текст появится здесь (локально, без облака).",
                color = colors.onKey.copy(alpha = 0.65f),
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 16.dp),
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                if (pinned.isNotEmpty()) {
                    SectionLabel("Закреплённые", colors.onKey)
                    pinned.forEach { entry ->
                        ClipboardRow(entry, colors.onKey, colors.key, onPaste, onPin)
                    }
                }
                if (recent.isNotEmpty()) {
                    SectionLabel("Недавние", colors.onKey)
                    recent.forEach { entry ->
                        ClipboardRow(entry, colors.onKey, colors.key, onPaste, onPin)
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(title: String, color: androidx.compose.ui.graphics.Color) {
    Text(
        text = title,
        color = color.copy(alpha = 0.7f),
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp),
    )
}

@Composable
private fun ClipboardRow(
    entry: ClipboardEntry,
    onKey: androidx.compose.ui.graphics.Color,
    keyBg: androidx.compose.ui.graphics.Color,
    onPaste: (String) -> Unit,
    onPin: (ClipboardEntry) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(keyBg, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = entry.text,
            color = onKey,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontSize = 14.sp,
            modifier = Modifier
                .weight(1f)
                .clickable { onPaste(entry.text) }
                .padding(8.dp),
        )
        Text(
            text = if (entry.pinned) "Открепить" else "Закрепить",
            color = onKey.copy(alpha = 0.75f),
            fontSize = 12.sp,
            modifier = Modifier
                .clickable { onPin(entry) }
                .padding(8.dp),
        )
    }
}
