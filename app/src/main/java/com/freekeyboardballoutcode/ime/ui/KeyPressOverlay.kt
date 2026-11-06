package com.freekeyboardballoutcode.ime.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

/** Press / long-press overlay state in root (screen) coordinates. */
data class KeyPressPreview(
    val label: String,
    val keyRoot: Offset,
    val keySize: Offset,
    val hasVariants: Boolean,
)

data class KeyVariantPicker(
    val variants: List<String>,
    val keyRoot: Offset,
    val keySize: Offset,
    val selectedIndex: Int,
    val columns: Int,
)

/**
 * Gboard Material You key-press popup: floating circle above the key.
 * Positioned in the keyboard-root coordinate space so top-row presses can
 * sit over the suggestion strip (not clipped inside the key row).
 */
@Composable
fun KeyPressPreviewOverlay(
    preview: KeyPressPreview,
    rootOrigin: Offset,
    bubbleColor: Color,
    contentColor: Color,
) {
    val density = LocalDensity.current
    val diameter = GboardMetrics.pressPopupDiameter
    val gap = GboardMetrics.pressPopupGap
    val diameterPx = with(density) { diameter.toPx() }
    val gapPx = with(density) { gap.toPx() }
    val localX = preview.keyRoot.x - rootOrigin.x
    val localY = preview.keyRoot.y - rootOrigin.y
    val x = (localX + preview.keySize.x / 2f - diameterPx / 2f).coerceAtLeast(0f).toInt()
    val y = (localY - diameterPx - gapPx).toInt()

    Box(
        modifier = Modifier
            .zIndex(30f)
            .offset { IntOffset(x, y) }
            .size(diameter)
            .shadow(4.dp, CircleShape, clip = false)
            .background(bubbleColor, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = preview.label,
                fontSize = GboardMetrics.pressPopupFontSize,
                fontWeight = FontWeight.Normal,
                color = contentColor,
            )
            if (preview.hasVariants) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    modifier = Modifier.padding(top = 2.dp),
                ) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .size(2.5.dp)
                                .background(contentColor.copy(alpha = 0.38f), CircleShape),
                        )
                    }
                }
            }
        }
    }
}
