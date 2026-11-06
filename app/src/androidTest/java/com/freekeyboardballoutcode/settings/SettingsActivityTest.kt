package com.freekeyboardballoutcode.settings

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsActivityTest {
    @get:Rule
    val rule = createAndroidComposeRule<SettingsActivity>()

    @Test
    fun showsRussianTitle() {
        rule.onNodeWithText("Настройки freekeyboard").assertExists()
    }
}
