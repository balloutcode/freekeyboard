package com.freekeyboardballoutcode.ime.ui

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.freekeyboardballoutcode.data.InputLanguage
import com.freekeyboardballoutcode.settings.SettingsActivity
import com.freekeyboardballoutcode.ui.theme.LocalKeyboardColors

/**
 * Gboard-like language sheet: radio list of enabled languages +
 * shortcut into language settings.
 */
@Composable
fun LanguagePicker(
    languages: Set<InputLanguage>,
    selected: InputLanguage,
    onSelect: (InputLanguage) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val colors = LocalKeyboardColors.current
    val ordered = rememberLanguageOrder(languages)

    Popup(
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 20.dp),
            shape = RoundedCornerShape(16.dp),
            color = colors.key,
            shadowElevation = 10.dp,
            tonalElevation = 2.dp,
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "Сменить язык",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onKey,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                )
                ordered.forEach { language ->
                    val isSelected = language == selected
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = isSelected,
                                onClick = { onSelect(language) },
                                role = Role.RadioButton,
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onSelect(language) },
                        )
                        Column(modifier = Modifier.padding(start = 4.dp, end = 12.dp)) {
                            Text(
                                text = language.pickerTitleRu,
                                style = MaterialTheme.typography.bodyLarge,
                                color = colors.onKey,
                            )
                            Text(
                                text = language.layoutNameRu,
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.onKey.copy(alpha = 0.62f),
                            )
                        }
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.padding(top = 4.dp),
                    color = colors.onKey.copy(alpha = 0.12f),
                )
                Text(
                    text = "Настройки языка",
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.onKey,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onDismiss()
                            val intent = Intent(context, SettingsActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        }
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                )
            }
        }
    }
}

@Composable
private fun rememberLanguageOrder(languages: Set<InputLanguage>): List<InputLanguage> {
    return InputLanguage.entries.filter { it in languages }
}

private val InputLanguage.pickerTitleRu: String
    get() = when (this) {
        InputLanguage.EN -> "Английский (США)"
        InputLanguage.RU -> "Русский"
    }

private val InputLanguage.layoutNameRu: String
    get() = when (this) {
        InputLanguage.EN -> "QWERTY"
        InputLanguage.RU -> "ЙЦУКЕН"
    }
