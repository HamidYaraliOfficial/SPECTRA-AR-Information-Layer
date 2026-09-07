package com.spectra.ar.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.spectra.ar.ui.theme.SpectraTheme
import com.spectra.ar.ui.theme.ThemePreferences
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun appearanceScreen_showsAllThreeAccentOptions() {
        composeTestRule.setContent {
            SpectraTheme(ThemePreferences()) {
                com.spectra.ar.ui.screens.settings.AppearanceSettingsScreen(onBack = {})
            }
        }
        composeTestRule.onNodeWithText("Windows Default").assertExists()
        composeTestRule.onNodeWithText("Blue").assertExists()
        composeTestRule.onNodeWithText("Red").assertExists()
    }
}
