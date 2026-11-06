package com.freekeyboardballoutcode.ime.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.freekeyboardballoutcode.ui.theme.LocalKeyboardColors
import kotlin.math.roundToInt

/**
 * Gboard-style long-press variant panel drawn inside the IME Compose tree.
 * White/key-colored rounded panel; selected glyph in a circular special-key highlight.
 */
@Composable
fun VariantPickerOverlay(
    variants: List<String>,
    selectedIndex: Int,
    localOffset: Offset,
    keySize: Offset,
    columns: Int = 4,
) {
    val colors = LocalKeyboardColors.current
    val density = LocalDensity.current
    val cell = 44.dp
    val cellPx = with(density) { cell.toPx() }
    val pad = 8.dp
    val cols = columns.coerceAtLeast(1).coerceAtMost(variants.size.coerceAtLeast(1))
    val rows = (variants.size + cols - 1) / cols
    val panelWidth = cell * cols + pad * 2
    val panelHeight = cell * rows + pad * 2
    val panelWidthPx = with(density) { panelWidth.toPx() }
    val panelHeightPx = with(density) { panelHeight.toPx() }

    // Anchor above the key (may overlap suggestion bar on the top row), like Gboard.
    val x = (localOffset.x + keySize.x / 2f - panelWidthPx / 2f)
        .coerceAtLeast(0f)
        .roundToInt()
    val y = (localOffset.y - panelHeightPx - with(density) { 6.dp.toPx() })
        .roundToInt()

    Box(
        modifier = Modifier
            .zIndex(20f)
            .offset { IntOffset(x, y) }
            .shadow(12.dp, RoundedCornerShape(24.dp))
            .background(colors.key, RoundedCornerShape(24.dp))
            .padding(pad)
            .widthIn(min = cell),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            for (row in 0 until rows) {
                Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                    for (col in 0 until cols) {
                        val index = row * cols + col
                        if (index >= variants.size) {
                            Box(Modifier.size(cell))
                        } else {
                            val selected = index == selectedIndex
                            Box(
                                modifier = Modifier
                                    .size(cell)
                                    .then(
                                        if (selected) {
                                            Modifier.background(colors.keySpecial, CircleShape)
                                        } else {
                                            Modifier
                                        },
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = variants[index],
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = colors.onKey,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Map a finger position (in root coords) to a variant index relative to the key. */
fun variantIndexForPointer(
    pointerRoot: Offset,
    keyRoot: Offset,
    keySize: Offset,
    variantCount: Int,
    columns: Int = 4,
    cellPx: Float,
    panelPadPx: Float,
): Int {
    if (variantCount <= 0) return 0
    val cols = columns.coerceAtLeast(1).coerceAtMost(variantCount)
    val rows = (variantCount + cols - 1) / cols
    val panelWidth = cellPx * cols + panelPadPx * 2
    val panelHeight = cellPx * rows + panelPadPx * 2
    val panelLeft = keyRoot.x + keySize.x / 2f - panelWidth / 2f
    val panelTop = keyRoot.y - panelHeight - 6f

    val localX = pointerRoot.x - panelLeft - panelPadPx
    val localY = pointerRoot.y - panelTop - panelPadPx
    val col = (localX / cellPx).toInt().coerceIn(0, cols - 1)
    val row = (localY / cellPx).toInt().coerceIn(0, rows - 1)
    val index = row * cols + col
    return index.coerceIn(0, variantCount - 1)
}
