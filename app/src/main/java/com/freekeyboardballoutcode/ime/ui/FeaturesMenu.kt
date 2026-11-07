package com.freekeyboardballoutcode.ime.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freekeyboardballoutcode.ui.theme.LocalKeyboardColors

/**
 * Offline features grid (A11-001): no mic / Google Search / GIF / Translate.
 */
@Composable
fun FeaturesMenu(
    clipboardEnabled: Boolean,
    oneHandedEnabled: Boolean,
    onTheme: () -> Unit,
    onClipboard: () -> Unit,
    onOneHanded: () -> Unit,
    onEmoji: () -> Unit,
    onSettings: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalKeyboardColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.background)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Функции",
            color = colors.onKey,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FeatureChip("Тема", Modifier.weight(1f), colors.key, colors.onKey, onTheme)
            FeatureChip("Эмодзи", Modifier.weight(1f), colors.key, colors.onKey, onEmoji)
            // Static label like other tiles; active state = filled chip (not "Обычный" action copy).
            FeatureChip(
                label = if (oneHandedEnabled) "Одной рукой ✓" else "Одной рукой",
                modifier = Modifier.weight(1f),
                background = if (oneHandedEnabled) colors.shiftActive else colors.key,
                content = if (oneHandedEnabled) colors.onShiftActive else colors.onKey,
                onClick = onOneHanded,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (clipboardEnabled) {
                FeatureChip("Буфер", Modifier.weight(1f), colors.key, colors.onKey, onClipboard)
            }
            FeatureChip("Настройки", Modifier.weight(1f), colors.key, colors.onKey, onSettings)
            FeatureChip("Закрыть", Modifier.weight(1f), colors.keySpecial, colors.onKey, onDismiss)
        }
    }
}

@Composable
private fun FeatureChip(
    label: String,
    modifier: Modifier,
    background: androidx.compose.ui.graphics.Color,
    content: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
) {
    Text(
        text = label,
        color = content,
        fontSize = 14.sp,
        modifier = modifier
            .background(background, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 14.dp),
    )
}
