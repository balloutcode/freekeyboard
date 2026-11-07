package com.freekeyboardballoutcode.ime.ui

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.freekeyboardballoutcode.data.ClipboardEntry
import com.freekeyboardballoutcode.data.KeyboardLayer
import com.freekeyboardballoutcode.data.OneHandedSide
import com.freekeyboardballoutcode.data.ThemePreference
import com.freekeyboardballoutcode.ime.input.KeyFeedback
import com.freekeyboardballoutcode.ime.keyboard.KeyAction
import com.freekeyboardballoutcode.ime.keyboard.KeyboardController
import com.freekeyboardballoutcode.settings.SettingsActivity
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
    onPersistTheme: suspend (ThemePreference) -> Unit = {},
    onPersistOneHanded: suspend (Boolean, OneHandedSide) -> Unit = { _, _ -> },
    onRejectSuggestion: suspend (String) -> Unit = {},
    onPinClipboard: suspend (ClipboardEntry) -> Unit = {},
    onRemoveClipboard: suspend (ClipboardEntry) -> Unit = {},
) {
    val state by controller.state.collectAsStateWithLifecycle()
    val view = LocalView.current
    val context = LocalContext.current

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
            val openSettings: () -> Unit = {
                controller.dismissFeaturesMenu()
                val launched = runCatching {
                    context.startActivity(
                        Intent(context, SettingsActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                    )
                }.isSuccess
                if (!launched) {
                    context.startActivity(
                        Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                    )
                }
            }

            val keyboardBody: @Composable () -> Unit = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    when {
                        state.showLanguagePicker -> {
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
                        state.showClipboardPanel -> {
                            ClipboardPanel(
                                entries = state.clipboardEntries,
                                onPaste = { controller.pasteClipboardEntry(it) },
                                onPin = { entry ->
                                    scope.launch { onPinClipboard(entry) }
                                },
                                onRemove = { entry ->
                                    scope.launch { onRemoveClipboard(entry) }
                                },
                                onClose = { controller.dismissClipboardPanel() },
                            )
                        }
                        state.layer == KeyboardLayer.EMOJI -> {
                            EmojiPanel(
                                recent = state.recentEmoji,
                                numberRow = state.effectiveNumberRow,
                                includeSuggestionStrip =
                                    state.showSuggestionStrip && !state.suppressSuggestions,
                                emojiFontScale = state.emojiFontScale,
                                onEmoji = { emoji ->
                                    KeyFeedback.play(
                                        view,
                                        state.soundOnKeypress,
                                        state.hapticOnKeypress,
                                        state.soundVolume,
                                        state.hapticStrength,
                                    )
                                    controller.onEmojiSelected(emoji)
                                    scope.launch { onPersistEmoji(emoji) }
                                },
                                onClose = { controller.closeEmoji() },
                            )
                        }
                        else -> {
                            if (state.showFeaturesMenu) {
                                FeaturesMenu(
                                    clipboardEnabled = state.clipboardHistoryEnabled &&
                                        !state.isPasswordField &&
                                        !state.suppressSuggestions &&
                                        !state.incognito,
                                    oneHandedEnabled = state.oneHandedMode,
                                    onTheme = {
                                        val next = controller.cycleTheme()
                                        scope.launch { onPersistTheme(next) }
                                    },
                                    onClipboard = { controller.openClipboardPanel() },
                                    onOneHanded = {
                                        val enabled = !state.oneHandedMode
                                        val side = state.oneHandedSide
                                        // Optimistic UI + immediate DataStore write so Settings
                                        // reflects the same source of truth as the IME.
                                        controller.setOneHandedMode(enabled, side)
                                        scope.launch { onPersistOneHanded(enabled, side) }
                                    },
                                    onEmoji = { controller.openEmojiFromMenu() },
                                    onSettings = openSettings,
                                    onDismiss = { controller.dismissFeaturesMenu() },
                                )
                            } else if (state.inlineSuggestionViews.isNotEmpty()) {
                                // Autofill chips (password managers) — show even when
                                // word suggestions are suppressed (password/email/URI).
                                SuggestionBar(
                                    suggestions = emptyList(),
                                    inlineViews = state.inlineSuggestionViews,
                                    onOpenFeatures = { controller.toggleFeaturesMenu() },
                                    incognito = state.incognito,
                                )
                            } else if (
                                !state.suppressSuggestions &&
                                state.layer == KeyboardLayer.LETTERS &&
                                state.showSuggestionStrip
                            ) {
                                // Full strip with candidates on letters (A10-006 / A16-001).
                                SuggestionBar(
                                    suggestions = state.suggestions,
                                    onSelect = { controller.acceptSuggestion(it) },
                                    onReject = { word ->
                                        controller.rejectSuggestion(word)
                                        scope.launch { onRejectSuggestion(word) }
                                    },
                                    onOpenFeatures = { controller.toggleFeaturesMenu() },
                                    incognito = state.incognito,
                                )
                            } else if (
                                !state.suppressSuggestions &&
                                (
                                    state.layer == KeyboardLayer.SYMBOLS ||
                                        state.layer == KeyboardLayer.NUMBER ||
                                        state.incognito ||
                                        (state.layer == KeyboardLayer.LETTERS && !state.showSuggestionStrip)
                                    )
                            ) {
                                // Keep strip chrome on ?123 (A10-008); empty chips / toolbar-only.
                                SuggestionBar(
                                    suggestions = emptyList(),
                                    onOpenFeatures = { controller.toggleFeaturesMenu() },
                                    incognito = state.incognito,
                                )
                            } else if (state.suppressSuggestions) {
                                // Password / email / phone / number: toolbar only (Features access).
                                SuggestionBar(
                                    suggestions = emptyList(),
                                    onOpenFeatures = { controller.toggleFeaturesMenu() },
                                    incognito = state.incognito,
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
                                        // Long-press comma / « → settings (Gboard parity).
                                        KeyFeedback.play(
                                            view,
                                            state.soundOnKeypress,
                                            state.hapticOnKeypress,
                                            state.soundVolume,
                                            state.hapticStrength,
                                        )
                                        val beforeLang = controller.currentLanguage()
                                        controller.onKey(key)
                                        val afterLang = controller.currentLanguage()
                                        if (afterLang != beforeLang) {
                                            scope.launch { onPersistLanguage(afterLang) }
                                        }
                                    }
                                },
                                onVariant = { text ->
                                    KeyFeedback.play(
                                        view,
                                        state.soundOnKeypress,
                                        state.hapticOnKeypress,
                                        state.soundVolume,
                                        state.hapticStrength,
                                    )
                                    if (text == "⚙" || text.equals("settings", true)) {
                                        openSettings()
                                    } else {
                                        controller.onVariantSelected(text)
                                    }
                                },
                                onOpenSettings = openSettings,
                                onSpaceLongPress = { controller.requestLanguagePicker() },
                                onSpaceCursorSwipe = { delta -> controller.onSpaceCursorSwipe(delta) },
                                onShiftLongPress = {
                                    KeyFeedback.play(
                                        view,
                                        state.soundOnKeypress,
                                        state.hapticOnKeypress,
                                        state.soundVolume,
                                        state.hapticStrength,
                                    )
                                    controller.onShiftLongPress()
                                },
                                onBackspaceRepeat = { byWord ->
                                    KeyFeedback.play(
                                        view,
                                        state.soundOnKeypress,
                                        state.hapticOnKeypress,
                                        state.soundVolume,
                                        state.hapticStrength,
                                    )
                                    controller.onBackspace(byWord = byWord)
                                },
                                onPressPreview = { pressPreview = it },
                                onVariantPicker = { variantPicker = it },
                            )
                        }
                    }
                }
            }

            if (state.oneHandedMode) {
                // ~68% width docked to side; empty gutter is intentional (Gboard-like).
                val dockEnd = state.oneHandedSide == OneHandedSide.RIGHT
                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.68f)
                            .align(if (dockEnd) Alignment.CenterEnd else Alignment.CenterStart),
                    ) {
                        if (dockEnd) {
                            OneHandRail(
                                modifier = Modifier.width(40.dp),
                                onExpand = {
                                    controller.setOneHandedMode(false)
                                    scope.launch { onPersistOneHanded(false, state.oneHandedSide) }
                                },
                                onFlip = {
                                    controller.flipOneHandedSide()
                                    scope.launch {
                                        onPersistOneHanded(true, controller.state.value.oneHandedSide)
                                    }
                                },
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) { keyboardBody() }
                        if (!dockEnd) {
                            OneHandRail(
                                modifier = Modifier.width(40.dp),
                                onExpand = {
                                    controller.setOneHandedMode(false)
                                    scope.launch { onPersistOneHanded(false, state.oneHandedSide) }
                                },
                                onFlip = {
                                    controller.flipOneHandedSide()
                                    scope.launch {
                                        onPersistOneHanded(true, controller.state.value.oneHandedSide)
                                    }
                                },
                            )
                        }
                    }
                }
            } else {
                keyboardBody()
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

        }
    }
}

@Composable
private fun OneHandRail(
    onExpand: () -> Unit,
    onFlip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalKeyboardColors.current
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(colors.keySpecial)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "↔",
            color = colors.onKey,
            modifier = Modifier
                .clickable(onClick = onFlip)
                .padding(8.dp),
        )
        Text(
            "⤢",
            color = colors.onKey,
            modifier = Modifier
                .clickable(onClick = onExpand)
                .padding(8.dp),
        )
    }
}
