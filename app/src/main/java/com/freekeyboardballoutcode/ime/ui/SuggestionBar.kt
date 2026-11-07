package com.freekeyboardballoutcode.ime.ui

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.freekeyboardballoutcode.ui.theme.LocalKeyboardColors

/**
 * Candidate strip with offline toolbar entry.
 * Long-press rejects a chip (A10-007). No swipe/drag gestures.
 * When [inlineViews] is non-empty, password-manager autofill chips replace word suggestions.
 */
@Composable
fun SuggestionBar(
    suggestions: List<String>,
    onSelect: (String) -> Unit = {},
    onReject: (String) -> Unit = {},
    onOpenFeatures: () -> Unit = {},
    incognito: Boolean = false,
    inlineViews: List<View> = emptyList(),
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
        Box(
            modifier = Modifier
                .size(GboardMetrics.suggestionBarHeight)
                .clickable(onClick = onOpenFeatures),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Apps,
                contentDescription = "Функции",
                tint = colors.onKey.copy(alpha = 0.75f),
                modifier = Modifier.size(22.dp),
            )
        }
        if (incognito) {
            Icon(
                imageVector = Icons.Filled.VisibilityOff,
                contentDescription = "Инкогнито",
                tint = colors.onKey.copy(alpha = 0.7f),
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(18.dp),
            )
        }
        if (inlineViews.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                inlineViews.forEach { chipView ->
                    AndroidView(
                        factory = { ctx ->
                            FrameLayout(ctx).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                )
                            }
                        },
                        update = { host ->
                            if (chipView.parent !== host) {
                                (chipView.parent as? ViewGroup)?.removeView(chipView)
                                host.removeAllViews()
                                host.addView(
                                    chipView,
                                    FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                    ),
                                )
                            }
                        },
                        modifier = Modifier.fillMaxHeight(),
                    )
                }
            }
            return
        }
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
                            Modifier
                                .pointerInput(word) {
                                    detectTapGestures(
                                        onTap = { onSelect(word) },
                                        onLongPress = { onReject(word) },
                                    )
                                }
                                .semantics(mergeDescendants = true) {
                                    contentDescription = "Предложение: $word"
                                    onClick(label = "Выбрать") {
                                        onSelect(word)
                                        true
                                    }
                                    customActions = listOf(
                                        CustomAccessibilityAction("Отклонить") {
                                            onReject(word)
                                            true
                                        },
                                    )
                                }
                        } else {
                            Modifier.clearAndSetSemantics { }
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
