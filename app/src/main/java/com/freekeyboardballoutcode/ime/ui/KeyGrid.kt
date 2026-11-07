package com.freekeyboardballoutcode.ime.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.freekeyboardballoutcode.data.InputLanguage
import com.freekeyboardballoutcode.data.ShiftMode
import com.freekeyboardballoutcode.ime.input.EnterKeyStyle
import com.freekeyboardballoutcode.ime.input.SpaceCursorSwipe
import com.freekeyboardballoutcode.ime.keyboard.KeyAction
import com.freekeyboardballoutcode.ime.keyboard.KeyDefinition
import com.freekeyboardballoutcode.ime.keyboard.KeyboardLayout
import com.freekeyboardballoutcode.ui.theme.LocalKeyboardColors
import kotlinx.coroutines.withTimeoutOrNull

@Composable
fun KeyGrid(
    layout: KeyboardLayout,
    caps: Boolean,
    shift: ShiftMode,
    enterStyle: EnterKeyStyle,
    language: InputLanguage,
    onKey: (KeyDefinition) -> Unit,
    onVariant: (String) -> Unit,
    onSpaceLongPress: () -> Unit,
    onSpaceCursorSwipe: (delta: Int) -> Unit = {},
    onShiftLongPress: () -> Unit = {},
    onBackspaceRepeat: (byWord: Boolean) -> Unit = {},
    onPressPreview: (KeyPressPreview?) -> Unit = {},
    onVariantPicker: (KeyVariantPicker?) -> Unit = {},
    @Suppress("UNUSED_PARAMETER")
    onOpenSettings: () -> Unit = {},
    keyBorders: Boolean = true,
    popupOnKeypress: Boolean = true,
    longPressDelayMs: Int = 300,
    modifier: Modifier = Modifier,
) {
    val colors = LocalKeyboardColors.current
    val fontScale = LocalConfiguration.current.fontScale.coerceIn(1f, 1.35f)
    val landscape = LocalConfiguration.current.orientation ==
        android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val keyHeight =
        (if (landscape) GboardMetrics.keyHeightLandscape else GboardMetrics.keyHeightPortrait) *
            fontScale
    val rowGap =
        (if (landscape) GboardMetrics.rowGapLandscape else GboardMetrics.rowGapPortrait) *
            fontScale
    val keyGap = if (landscape) GboardMetrics.keyGapLandscape else GboardMetrics.keyGapPortrait
    val sidePad = if (landscape) GboardMetrics.sidePadLandscape else GboardMetrics.sidePadPortrait
    var activePicker by remember { mutableStateOf<KeyVariantPicker?>(null) }

    Box(
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = sidePad,
                    end = sidePad,
                    top = GboardMetrics.gridVerticalPad,
                    bottom = GboardMetrics.gridVerticalPad,
                ),
            verticalArrangement = Arrangement.spacedBy(rowGap),
        ) {
            layout.rows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(keyGap),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (row.startInsetWeight > 0f) {
                        Spacer(Modifier.weight(row.startInsetWeight))
                    }
                    row.keys.forEach { key ->
                        val shiftActive = shift == ShiftMode.ONCE || shift == ShiftMode.CAPS
                        val label = when {
                            key.action == KeyAction.COMMIT && key.output.length == 1 &&
                                key.output.first().isLetter() -> {
                                if (caps) key.output.uppercase() else key.output
                            }
                            // Gboard: spacebar shows the active input language name.
                            key.action == KeyAction.SPACE -> language.displayNameRu
                            else -> key.label
                        }
                        val displayVariants = if (caps) {
                            key.variants.map { v ->
                                if (v.length == 1 && v.first().isLetter()) v.uppercase() else v
                            }
                        } else {
                            key.variants
                        }
                        // Letters only: symbols Shift toggles pages and must not look "latched".
                        val highlighted = key.action == KeyAction.SHIFT &&
                            shiftActive &&
                            !layout.id.startsWith("symbols")
                        val showPreview = key.action == KeyAction.COMMIT &&
                            key.output.isNotEmpty() &&
                            key.output != " "
                        // A20-001/A20-004: TalkBack / Switch Access description + click action.
                        val accessibilityLabel = KeyAccessibility.describe(
                            key = key,
                            shift = shift,
                            enterStyle = enterStyle,
                            label = label,
                        )
                        KeyButton(
                            label = label,
                            hint = key.hint,
                            icon = keyIcon(key, shift, enterStyle, layoutId = layout.id),
                            accessibilityLabel = accessibilityLabel,
                            special = key.isSpecial,
                            weight = key.weight,
                            height = keyHeight,
                            shape = keyShape(key, enterStyle),
                            circular = isCircularActionKey(key, enterStyle),
                            background = when {
                                highlighted -> colors.shiftActive
                                key.isSpecial -> colors.keySpecial
                                else -> colors.key
                            },
                            keyboardBackground = colors.background,
                            contentColor = if (highlighted) colors.onShiftActive else colors.onKey,
                            borderColor = if (keyBorders) colors.keyBorder else Color.Transparent,
                            showBorder = keyBorders,
                            longPressDelayMs = longPressDelayMs,
                            onTap = { onKey(key) },
                            onPressChange = { pressing, pos, size ->
                                onPressPreview(
                                    if (pressing && showPreview && popupOnKeypress) {
                                        KeyPressPreview(label, pos, size, displayVariants.isNotEmpty())
                                    } else {
                                        null
                                    },
                                )
                            },
                            // Space: tap=space, horizontal swipe=move caret, LP=language picker.
                            // Globe LP = picker; Shift LP = Caps Lock.
                            onSpaceLongPress = when (key.action) {
                                KeyAction.SPACE, KeyAction.LANGUAGE -> onSpaceLongPress
                                KeyAction.SHIFT -> onShiftLongPress
                                else -> null
                            },
                            onSpaceCursorSwipe = if (key.action == KeyAction.SPACE) {
                                onSpaceCursorSwipe
                            } else {
                                null
                            },
                            onHoldRepeat = if (key.action == KeyAction.BACKSPACE) {
                                { byWord -> onBackspaceRepeat(byWord) }
                            } else {
                                null
                            },
                            variants = displayVariants,
                            onVariantSession = { active, pos, size, selected, columns ->
                                if (active && displayVariants.isNotEmpty()) {
                                    onPressPreview(null)
                                    val next = KeyVariantPicker(
                                        variants = displayVariants,
                                        keyRoot = pos,
                                        keySize = size,
                                        selectedIndex = selected,
                                        columns = columns,
                                    )
                                    activePicker = next
                                    onVariantPicker(next)
                                } else if (!active) {
                                    val commit = activePicker?.let { state ->
                                        state.variants.getOrNull(state.selectedIndex)
                                    }
                                    activePicker = null
                                    onVariantPicker(null)
                                    onPressPreview(null)
                                    if (commit != null) onVariant(commit)
                                }
                            },
                            onVariantSelect = { index ->
                                val next = activePicker?.copy(selectedIndex = index)
                                activePicker = next
                                onVariantPicker(next)
                            },
                        )
                    }
                    if (row.endInsetWeight > 0f) {
                        Spacer(Modifier.weight(row.endInsetWeight))
                    }
                }
            }
        }

    }
}

@Composable
private fun RowScope.KeyButton(
    label: String,
    hint: String?,
    icon: ImageVector?,
    accessibilityLabel: String?,
    special: Boolean,
    weight: Float,
    height: Dp,
    shape: Shape,
    circular: Boolean,
    background: Color,
    keyboardBackground: Color,
    contentColor: Color,
    borderColor: Color,
    showBorder: Boolean,
    longPressDelayMs: Int,
    onTap: () -> Unit,
    onPressChange: (Boolean, Offset, Offset) -> Unit,
    onSpaceLongPress: (() -> Unit)?,
    onSpaceCursorSwipe: ((delta: Int) -> Unit)?,
    onHoldRepeat: ((byWord: Boolean) -> Unit)?,
    variants: List<String>,
    onVariantSession: (active: Boolean, pos: Offset, size: Offset, selected: Int, columns: Int) -> Unit,
    onVariantSelect: (Int) -> Unit,
) {
    var pressed by remember { mutableStateOf(false) }
    var position by remember { mutableStateOf(Offset.Zero) }
    var keySize by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current
    // Gboard: pressed key face dissolves into keyboard background; letter nearly gone.
    val press by animateFloatAsState(
        targetValue = if (pressed) 1f else 0f,
        animationSpec = tween(durationMillis = if (pressed) 25 else 70),
        label = "keyPress",
    )
    val faceColor = androidx.compose.ui.graphics.lerp(background, keyboardBackground, 0.96f * press)
    val contentAlpha = 1f - 0.88f * press
    val columns = when {
        variants.size <= 1 -> 1
        variants.size <= 4 -> variants.size
        variants.size <= 8 -> 4
        else -> 5
    }

    Box(
        modifier = Modifier
            .weight(weight)
            .height(height)
            .zIndex(if (pressed) 2f else 0f)
            .pointerInput(
                label,
                variants,
                longPressDelayMs,
                onHoldRepeat != null,
                onSpaceCursorSwipe != null,
            ) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    down.consume()
                    pressed = true
                    onPressChange(true, position, keySize)

                    // Space: swipe → caret; stationary hold → language picker; tap → space.
                    // Must use timeouts: awaitPointerEvent alone never wakes on a still finger.
                    if (onSpaceCursorSwipe != null && onSpaceLongPress != null) {
                        val thresholdPx = with(density) { SpaceCursorSwipe.THRESHOLD_DP.dp.toPx() }
                        val stepPx = with(density) { SpaceCursorSwipe.STEP_DP.dp.toPx() }
                        var totalDx = 0f
                        var emitted = 0
                        var swipeMode = false
                        var longPressFired = false
                        val deadlineMs = System.currentTimeMillis() + longPressDelayMs
                        try {
                            while (true) {
                                val remaining = (deadlineMs - System.currentTimeMillis()).coerceAtLeast(0L)
                                val event = if (!swipeMode && !longPressFired) {
                                    withTimeoutOrNull(remaining.coerceAtLeast(1L)) {
                                        awaitPointerEvent()
                                    }
                                } else {
                                    awaitPointerEvent()
                                }
                                if (event == null) {
                                    // Stationary hold reached long-press delay.
                                    if (!swipeMode && !longPressFired) {
                                        longPressFired = true
                                        pressed = false
                                        onPressChange(false, position, keySize)
                                        onSpaceLongPress()
                                    }
                                    continue
                                }
                                val change = event.changes.firstOrNull() ?: break
                                if (change.changedToUp()) {
                                    change.consume()
                                    break
                                }
                                if (change.pressed) {
                                    val dx = change.positionChange().x
                                    if (dx != 0f) {
                                        totalDx += dx
                                        change.consume()
                                    }
                                    if (!swipeMode &&
                                        SpaceCursorSwipe.isSwipeEngaged(totalDx, thresholdPx)
                                    ) {
                                        swipeMode = true
                                    }
                                    if (swipeMode) {
                                        val delta = SpaceCursorSwipe.deltaSteps(
                                            totalDxPx = totalDx,
                                            stepPx = stepPx,
                                            thresholdPx = thresholdPx,
                                            alreadyEmittedSteps = emitted,
                                        )
                                        if (delta != 0) {
                                            onSpaceCursorSwipe(delta)
                                            emitted += delta
                                        }
                                    }
                                }
                            }
                        } finally {
                            pressed = false
                            onPressChange(false, position, keySize)
                            if (!swipeMode && !longPressFired) {
                                onTap()
                            }
                        }
                        return@awaitEachGesture
                    }

                    val upBeforeLong = withTimeoutOrNull(longPressDelayMs.toLong()) {
                        waitForUpOrCancellation()
                    }
                    if (upBeforeLong != null) {
                        pressed = false
                        onPressChange(false, position, keySize)
                        onTap()
                        return@awaitEachGesture
                    }

                    // Long-press reached.
                    when {
                        onSpaceLongPress != null -> {
                            pressed = false
                            onPressChange(false, position, keySize)
                            onSpaceLongPress()
                            waitForUpOrCancellation()
                        }
                        onHoldRepeat != null -> {
                            // Gboard-like backspace: repeat chars, then words while held.
                            var elapsed = 0L
                            var interval = 55L
                            val wordAfterMs = 900L
                            try {
                                while (true) {
                                    val byWord = elapsed >= wordAfterMs
                                    onHoldRepeat(byWord)
                                    val up = withTimeoutOrNull(interval) {
                                        waitForUpOrCancellation()
                                    }
                                    if (up != null) break
                                    elapsed += interval
                                    if (!byWord) {
                                        interval = (interval - 4L).coerceAtLeast(28L)
                                    } else {
                                        interval = 90L
                                    }
                                }
                            } finally {
                                pressed = false
                                onPressChange(false, position, keySize)
                            }
                        }
                        variants.isNotEmpty() -> {
                            var selected = 0
                            onVariantSession(true, position, keySize, selected, columns)
                            val cellPx = with(density) { 44.dp.toPx() }
                            val padPx = with(density) { 8.dp.toPx() }
                            try {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    val change = event.changes.firstOrNull() ?: break
                                    if (change.changedToUp()) {
                                        change.consume()
                                        break
                                    }
                                    if (change.pressed) {
                                        val pointerRoot = Offset(
                                            position.x + change.position.x,
                                            position.y + change.position.y,
                                        )
                                        selected = variantIndexForPointer(
                                            pointerRoot = pointerRoot,
                                            keyRoot = position,
                                            keySize = keySize,
                                            variantCount = variants.size,
                                            columns = columns,
                                            cellPx = cellPx,
                                            panelPadPx = padPx,
                                        )
                                        onVariantSelect(selected)
                                        if (change.positionChange() != Offset.Zero) change.consume()
                                    }
                                }
                            } finally {
                                onVariantSession(false, position, keySize, selected, columns)
                                pressed = false
                                onPressChange(false, position, keySize)
                            }
                        }
                        else -> {
                            // Long press with no variants: treat as held tap on release.
                            waitForUpOrCancellation()
                            pressed = false
                            onPressChange(false, position, keySize)
                            onTap()
                        }
                    }
                }
            }
            // A20-001/002/004: TalkBack and Switch Access activate keys via an
            // accessibility click action rather than raw touch events; this runs
            // alongside (never instead of) the pointerInput gesture above.
            .then(
                if (accessibilityLabel != null) {
                    Modifier.semantics(mergeDescendants = true) {
                        contentDescription = accessibilityLabel
                        onClick(label = null) {
                            onTap()
                            true
                        }
                    }
                } else {
                    // Inert spacer (e.g. RU number-row pad NOOP): hide from a11y tree.
                    Modifier.clearAndSetSemantics { hideFromAccessibility() }
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        val faceModifier = if (circular) {
            Modifier.size(height)
        } else {
            Modifier.fillMaxSize()
        }
        Box(
            modifier = faceModifier
                .onGloballyPositioned {
                    position = it.positionInRoot()
                    keySize = Offset(it.size.width.toFloat(), it.size.height.toFloat())
                }
                .shadow(
                    elevation = if (pressed || !showBorder) 0.dp else 0.5.dp,
                    shape = shape,
                    clip = false,
                )
                .clip(shape)
                .background(faceColor)
                .then(
                    if (showBorder) {
                        Modifier.border(1.dp, borderColor.copy(alpha = 0.55f), shape)
                    } else {
                        Modifier
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (hint != null && icon == null) {
                Text(
                    text = hint,
                    color = contentColor.copy(alpha = 0.40f * contentAlpha),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 3.dp, end = 5.dp),
                )
            }
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    // Decorative: the parent Box's semantics (accessibilityLabel) already
                    // carries the TalkBack description for this key (A20-001).
                    contentDescription = null,
                    tint = contentColor.copy(alpha = contentAlpha),
                    modifier = Modifier.size(
                        if (special) GboardMetrics.keyIconSizeSpecial else GboardMetrics.keyIconSize,
                    ),
                )
            } else {
                Text(
                    text = label,
                    color = contentColor.copy(alpha = contentAlpha),
                    fontSize = when {
                        label.length > 3 -> GboardMetrics.wideLabelFontSize
                        else -> GboardMetrics.letterFontSize
                    },
                    fontWeight = FontWeight.Normal,
                )
            }
        }
    }
}

private fun isCircularActionKey(key: KeyDefinition, enterStyle: EnterKeyStyle): Boolean =
    key.action == KeyAction.ENTER && enterStyle != EnterKeyStyle.ENTER

private fun keyShape(
    key: KeyDefinition,
    enterStyle: EnterKeyStyle,
): Shape = when {
    isCircularActionKey(key, enterStyle) -> RoundedCornerShape(percent = 50)
    key.action == KeyAction.SPACE -> RoundedCornerShape(percent = 50)
    key.action == KeyAction.EMOJI -> RoundedCornerShape(GboardMetrics.letterCornerRadius)
    else -> RoundedCornerShape(GboardMetrics.letterCornerRadius)
}

private fun keyIcon(
    key: KeyDefinition,
    shift: ShiftMode,
    enterStyle: EnterKeyStyle,
    layoutId: String,
): ImageVector? = when (key.action) {
    // Symbols Shift is a page toggle ("=\<" / "?123") — show its text label, not a caps glyph.
    KeyAction.SHIFT -> if (layoutId.startsWith("symbols")) {
        null
    } else {
        when (shift) {
            ShiftMode.OFF -> KeyboardIcons.ShiftOutline
            ShiftMode.ONCE -> KeyboardIcons.Shift
            ShiftMode.CAPS -> KeyboardIcons.CapsLock
        }
    }
    KeyAction.BACKSPACE -> Icons.AutoMirrored.Filled.Backspace
    KeyAction.ENTER -> when (enterStyle) {
        EnterKeyStyle.SEND -> Icons.AutoMirrored.Filled.Send
        EnterKeyStyle.SEARCH -> Icons.Filled.Search
        EnterKeyStyle.DONE -> Icons.Filled.Check
        EnterKeyStyle.GO, EnterKeyStyle.NEXT -> Icons.Filled.ArrowForward
        EnterKeyStyle.ENTER -> Icons.AutoMirrored.Filled.KeyboardReturn
    }
    KeyAction.EMOJI -> Icons.Outlined.EmojiEmotions
    KeyAction.LANGUAGE -> Icons.Filled.Language
    else -> null
}
