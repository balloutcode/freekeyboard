package com.freekeyboardballoutcode.ime.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.freekeyboardballoutcode.data.KeyboardLayer
import com.freekeyboardballoutcode.ime.input.KeyFeedback
import com.freekeyboardballoutcode.ime.keyboard.KeyAction
import com.freekeyboardballoutcode.ime.keyboard.KeyboardController
import com.freekeyboardballoutcode.ui.theme.FreekeyboardTheme
import com.freekeyboardballoutcode.ui.theme.LocalKeyboardColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun KeyboardRoot(
    controller: KeyboardController,
    scope: CoroutineScope,
    onPersistLanguage: suspend (com.freekeyboardballoutcode.data.InputLanguage) -> Unit,
    onPersistEmoji: suspend (String) -> Unit,
) {
    val state by controller.state.collectAsStateWithLifecycle()
    val view = LocalView.current

    FreekeyboardTheme(theme = state.theme) {
        val colors = LocalKeyboardColors.current
        var rootOrigin by remember { mutableStateOf(Offset.Zero) }
        var pressPreview by remember { mutableStateOf<KeyPressPreview?>(null) }
        var variantPicker by remember { mutableStateOf<KeyVariantPicker?>(null) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.background)
                .onGloballyPositioned { rootOrigin = it.positionInRoot() },
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                when (state.layer) {
                    KeyboardLayer.EMOJI -> EmojiPanel(
                        recent = state.recentEmoji,
                        numberRow = state.numberRow,
                        includeSuggestionStrip = state.showSuggestionStrip,
                        onEmoji = { emoji ->
                            KeyFeedback.play(view, state.soundOnKeypress, state.hapticOnKeypress)
                            controller.onEmojiSelected(emoji)
                            scope.launch { onPersistEmoji(emoji) }
                        },
                        onClose = { controller.closeEmoji() },
                    )
                    else -> {
                        if (state.showSuggestionStrip && state.layer == KeyboardLayer.LETTERS) {
                            SuggestionBar(
                                suggestions = state.suggestions,
                                onSelect = { controller.acceptSuggestion(it) },
                            )
                        }
                        KeyGrid(
                            layout = state.activeLayout,
                            caps = state.isCaps,
                            shift = state.shift,
                            enterStyle = state.enterStyle,
                            language = state.language,
                            keyBorders = state.keyBorders,
                            popupOnKeypress = state.popupOnKeypress,
                            longPressDelayMs = state.longPressDelayMs,
                            onKey = { key ->
                                if (key.action != KeyAction.NOOP) {
                                    KeyFeedback.play(view, state.soundOnKeypress, state.hapticOnKeypress)
                                    val beforeLang = controller.currentLanguage()
                                    controller.onKey(key)
                                    val afterLang = controller.currentLanguage()
                                    if (afterLang != beforeLang) {
                                        scope.launch { onPersistLanguage(afterLang) }
                                    }
                                }
                            },
                            onVariant = { text ->
                                KeyFeedback.play(view, state.soundOnKeypress, state.hapticOnKeypress)
                                controller.onVariantSelected(text)
                            },
                            // Short tap globe toggles EN↔RU; long-press globe/space opens picker.
                            onSpaceLongPress = { controller.requestLanguagePicker() },
                            // Gboard: double-tap Shift → Caps; long-press Shift also locks Caps.
                            onShiftLongPress = {
                                KeyFeedback.play(view, state.soundOnKeypress, state.hapticOnKeypress)
                                controller.onShiftLongPress()
                            },
                            onBackspaceRepeat = { byWord ->
                                KeyFeedback.play(view, state.soundOnKeypress, state.hapticOnKeypress)
                                controller.onBackspace(byWord = byWord)
                            },
                            onPressPreview = { pressPreview = it },
                            onVariantPicker = { variantPicker = it },
                        )
                    }
                }
            }

            pressPreview?.let { preview ->
                KeyPressPreviewOverlay(
                    preview = preview,
                    rootOrigin = rootOrigin,
                    bubbleColor = colors.key,
                    contentColor = colors.onKey,
                )
            }

            variantPicker?.let { picker ->
                VariantPickerOverlay(
                    variants = picker.variants,
                    selectedIndex = picker.selectedIndex,
                    localOffset = Offset(
                        picker.keyRoot.x - rootOrigin.x,
                        picker.keyRoot.y - rootOrigin.y,
                    ),
                    keySize = picker.keySize,
                    columns = picker.columns,
                )
            }

            if (state.showLanguagePicker) {
                LanguagePicker(
                    languages = state.enabledLanguages,
                    selected = state.language,
                    onSelect = { language ->
                        controller.selectLanguage(language)
                        scope.launch { onPersistLanguage(language) }
                    },
                    onDismiss = { controller.dismissLanguagePicker() },
                )
            }
        }
    }
}
