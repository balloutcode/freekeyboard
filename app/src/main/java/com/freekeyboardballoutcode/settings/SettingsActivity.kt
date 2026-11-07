package com.freekeyboardballoutcode.settings

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.freekeyboardballoutcode.R
import com.freekeyboardballoutcode.data.ThemePreference
import com.freekeyboardballoutcode.data.UserPreferencesRepository
import com.freekeyboardballoutcode.settings.ui.SettingsScreens
import com.freekeyboardballoutcode.ui.theme.DarkBackground
import com.freekeyboardballoutcode.ui.theme.FreekeyboardTheme
import com.freekeyboardballoutcode.ui.theme.LightBackground
import com.freekeyboardballoutcode.ui.theme.OledBackground
import kotlinx.coroutines.launch

class SettingsActivity : ComponentActivity() {
    private lateinit var prefs: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        prefs = UserPreferencesRepository(applicationContext)

        setContent {
            val state by prefs.preferences.collectAsStateWithLifecycle(
                initialValue = com.freekeyboardballoutcode.data.UserPreferences(),
            )
            val theme = state.theme
            val lightBars = theme == ThemePreference.LIGHT
            val scrim = when (theme) {
                ThemePreference.LIGHT -> LightBackground.toArgb()
                ThemePreference.DARK -> DarkBackground.toArgb()
                ThemePreference.OLED_DARK -> OledBackground.toArgb()
            }
            DisposableEffect(theme) {
                enableEdgeToEdge(
                    statusBarStyle = if (lightBars) {
                        SystemBarStyle.light(scrim, scrim)
                    } else {
                        SystemBarStyle.dark(scrim)
                    },
                    navigationBarStyle = if (lightBars) {
                        SystemBarStyle.light(scrim, scrim)
                    } else {
                        SystemBarStyle.dark(scrim)
                    },
                )
                onDispose { }
            }
            FreekeyboardTheme(theme = theme) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                ) { padding ->
                    SettingsScreens(
                        preferences = state,
                        modifier = Modifier.padding(padding),
                        onThemeSelected = { selected ->
                            lifecycleScope.launch { prefs.setTheme(selected) }
                        },
                        onLanguageToggled = { language, enabled ->
                            lifecycleScope.launch {
                                val ok = prefs.setLanguageEnabled(language, enabled)
                                if (!ok) {
                                    Toast.makeText(
                                        this@SettingsActivity,
                                        R.string.language_last_warning,
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                }
                            }
                        },
                        onToggle = { key, value ->
                            lifecycleScope.launch { prefs.setBoolean(key, value) }
                        },
                        onLongPressDelay = { ms ->
                            lifecycleScope.launch { prefs.setLongPressDelayMs(ms) }
                        },
                        onSoundVolume = { v ->
                            lifecycleScope.launch { prefs.setSoundVolume(v) }
                        },
                        onHapticStrength = { v ->
                            lifecycleScope.launch { prefs.setHapticStrength(v) }
                        },
                        onEmojiFontScale = { v ->
                            lifecycleScope.launch { prefs.setEmojiFontScale(v) }
                        },
                        onAddPersonalWord = { w ->
                            lifecycleScope.launch { prefs.addPersonalWord(w) }
                        },
                        onRemovePersonalWord = { w ->
                            lifecycleScope.launch { prefs.removePersonalWord(w) }
                        },
                        onOpenImeSettings = {
                            startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
                        },
                        onSelectIme = {
                            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.showInputMethodPicker()
                        },
                    )
                }
            }
        }
    }
}
