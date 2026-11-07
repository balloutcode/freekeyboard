package com.freekeyboardballoutcode.ime.ui

import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freekeyboardballoutcode.data.InputLanguage
import com.freekeyboardballoutcode.settings.SettingsActivity
import com.freekeyboardballoutcode.ui.theme.LocalKeyboardColors

/**
 * In-IME language sheet (not a Popup window — Popups are unreliable inside InputMethodService).
 */
@Composable
fun LanguagePicker(
    languages: Set<InputLanguage>,
    selected: InputLanguage,
    onSelect: (InputLanguage) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val colors = LocalKeyboardColors.current
    val ordered = InputLanguage.entries.filter { it in languages }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.background)
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Сменить язык",
                color = colors.onKey,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "Закрыть",
                color = colors.onKey.copy(alpha = 0.75f),
                fontSize = 14.sp,
                modifier = Modifier
                    .clickable(onClick = onDismiss)
                    .padding(8.dp),
            )
        }
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
                    .background(
                        if (isSelected) colors.keySpecial else colors.key,
                        RoundedCornerShape(10.dp),
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
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
            modifier = Modifier.padding(vertical = 8.dp),
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
                    context.startActivity(
                        Intent(context, SettingsActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                    )
                }
                .padding(horizontal = 12.dp, vertical = 12.dp),
        )
    }
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
