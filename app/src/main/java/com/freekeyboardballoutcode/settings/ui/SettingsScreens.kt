package com.freekeyboardballoutcode.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.freekeyboardballoutcode.R
import com.freekeyboardballoutcode.data.InputLanguage
import com.freekeyboardballoutcode.data.ThemePreference
import com.freekeyboardballoutcode.data.UserPreferences

@Composable
fun SettingsScreens(
    preferences: UserPreferences,
    onThemeSelected: (ThemePreference) -> Unit,
    onLanguageToggled: (InputLanguage, Boolean) -> Unit,
    onToggle: (String, Boolean) -> Unit,
    onLongPressDelay: (Int) -> Unit,
    onOpenImeSettings: () -> Unit,
    onSelectIme: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.titleLarge,
        )

        SectionTitle(stringResource(R.string.settings_section_setup))
        Button(onClick = onOpenImeSettings, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.settings_enable_ime))
        }
        Button(onClick = onSelectIme, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.settings_select_ime))
        }

        HorizontalDivider()
        SectionTitle(stringResource(R.string.settings_section_languages))
        LanguageToggle(
            label = stringResource(R.string.language_en),
            checked = InputLanguage.EN in preferences.enabledLanguages,
            onCheckedChange = { onLanguageToggled(InputLanguage.EN, it) },
        )
        LanguageToggle(
            label = stringResource(R.string.language_ru),
            checked = InputLanguage.RU in preferences.enabledLanguages,
            onCheckedChange = { onLanguageToggled(InputLanguage.RU, it) },
        )

        HorizontalDivider()
        SectionTitle(stringResource(R.string.settings_section_theme))
        ThemeOption(
            label = stringResource(R.string.theme_light),
            selected = preferences.theme == ThemePreference.LIGHT,
            onClick = { onThemeSelected(ThemePreference.LIGHT) },
        )
        ThemeOption(
            label = stringResource(R.string.theme_dark),
            selected = preferences.theme == ThemePreference.DARK,
            onClick = { onThemeSelected(ThemePreference.DARK) },
        )
        ThemeOption(
            label = stringResource(R.string.theme_oled),
            selected = preferences.theme == ThemePreference.OLED_DARK,
            onClick = { onThemeSelected(ThemePreference.OLED_DARK) },
        )
        PrefSwitch(
            title = stringResource(R.string.settings_key_borders),
            checked = preferences.keyBorders,
            onCheckedChange = { onToggle("key_borders", it) },
        )

        HorizontalDivider()
        SectionTitle(stringResource(R.string.settings_section_preferences))
        SubHeader(stringResource(R.string.settings_keys_header))
        PrefSwitch(
            title = stringResource(R.string.settings_number_row),
            summary = stringResource(R.string.settings_number_row_summary),
            checked = preferences.numberRow,
            onCheckedChange = { onToggle("number_row", it) },
        )
        PrefSwitch(
            title = stringResource(R.string.settings_show_emoji_key),
            summary = stringResource(R.string.settings_show_emoji_key_summary),
            checked = preferences.showEmojiKey,
            onCheckedChange = { onToggle("show_emoji_key", it) },
        )
        SubHeader(stringResource(R.string.settings_keypress_header))
        PrefSwitch(
            title = stringResource(R.string.settings_sound),
            checked = preferences.soundOnKeypress,
            onCheckedChange = { onToggle("sound_on_keypress", it) },
        )
        PrefSwitch(
            title = stringResource(R.string.settings_haptic),
            checked = preferences.hapticOnKeypress,
            onCheckedChange = { onToggle("haptic_on_keypress", it) },
        )
        PrefSwitch(
            title = stringResource(R.string.settings_popup),
            checked = preferences.popupOnKeypress,
            onCheckedChange = { onToggle("popup_on_keypress", it) },
        )
        PrefSwitch(
            title = stringResource(R.string.settings_long_press_symbols),
            summary = stringResource(R.string.settings_long_press_symbols_summary),
            checked = preferences.longPressForSymbols,
            onCheckedChange = { onToggle("long_press_for_symbols", it) },
        )
        Text(
            text = stringResource(R.string.settings_long_press_delay),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text = stringResource(R.string.settings_long_press_delay_value, preferences.longPressDelayMs),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        )
        Slider(
            value = preferences.longPressDelayMs.toFloat(),
            onValueChange = { onLongPressDelay(it.toInt()) },
            valueRange = 100f..700f,
            steps = 11,
            modifier = Modifier.fillMaxWidth(),
        )

        HorizontalDivider()
        SectionTitle(stringResource(R.string.settings_section_text_correction))
        SubHeader(stringResource(R.string.settings_suggestions_header))
        PrefSwitch(
            title = stringResource(R.string.settings_show_suggestions),
            checked = preferences.showSuggestionStrip,
            onCheckedChange = { onToggle("show_suggestion_strip", it) },
        )
        PrefSwitch(
            title = stringResource(R.string.settings_next_word),
            checked = preferences.nextWordSuggestions,
            onCheckedChange = { onToggle("next_word_suggestions", it) },
        )
        PrefSwitch(
            title = stringResource(R.string.settings_block_offensive),
            checked = preferences.blockOffensiveWords,
            onCheckedChange = { onToggle("block_offensive_words", it) },
        )
        SubHeader(stringResource(R.string.settings_corrections_header))
        PrefSwitch(
            title = stringResource(R.string.settings_auto_cap),
            summary = stringResource(R.string.settings_auto_cap_summary),
            checked = preferences.autoCapitalization,
            onCheckedChange = { onToggle("auto_capitalization", it) },
        )
        PrefSwitch(
            title = stringResource(R.string.settings_double_space),
            checked = preferences.doubleSpacePeriod,
            onCheckedChange = { onToggle("double_space_period", it) },
        )

        HorizontalDivider()
        SectionTitle(stringResource(R.string.settings_about))
        Text(
            text = stringResource(R.string.settings_about_body),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleMedium)
}

@Composable
private fun SubHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 4.dp),
    )
}

@Composable
private fun ThemeOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(text = label, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
private fun LanguageToggle(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    PrefSwitch(title = label, checked = checked, onCheckedChange = onCheckedChange)
}

@Composable
private fun PrefSwitch(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    summary: String? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            if (summary != null) {
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                )
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
