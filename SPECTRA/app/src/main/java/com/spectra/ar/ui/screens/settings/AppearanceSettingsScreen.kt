package com.spectra.ar.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.spectra.ar.R
import com.spectra.ar.ui.theme.AccentColor
import com.spectra.ar.ui.theme.ThemeMode

@Composable
fun AppearanceSettingsScreen(onBack: () -> Unit, viewModel: AppearanceSettingsViewModel = hiltViewModel()) {
    val prefs by viewModel.themePreferences.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_appearance)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null) } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text(stringResource(R.string.appearance_theme_mode))
            listOf(
                ThemeMode.SYSTEM to R.string.appearance_mode_system,
                ThemeMode.LIGHT to R.string.appearance_mode_light,
                ThemeMode.DARK to R.string.appearance_mode_dark
            ).forEach { (mode, labelRes) ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    RadioButton(selected = prefs.mode == mode, onClick = { viewModel.setMode(mode) })
                    Text(stringResource(labelRes), modifier = Modifier.padding(top = 12.dp))
                }
            }

            Text(stringResource(R.string.appearance_accent), modifier = Modifier.padding(top = 16.dp))
            listOf(
                AccentColor.WINDOWS_DEFAULT to R.string.appearance_accent_default,
                AccentColor.BLUE to R.string.appearance_accent_blue,
                AccentColor.RED to R.string.appearance_accent_red
            ).forEach { (accent, labelRes) ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    RadioButton(selected = prefs.accent == accent, onClick = { viewModel.setAccent(accent) })
                    Text(stringResource(labelRes), modifier = Modifier.padding(top = 12.dp))
                }
            }

            ListItem(
                headlineContent = { Text(stringResource(R.string.appearance_dynamic_color)) },
                trailingContent = { Switch(checked = prefs.dynamicColor, onCheckedChange = viewModel::setDynamicColor) }
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.appearance_amoled)) },
                trailingContent = { Switch(checked = prefs.amoled, onCheckedChange = viewModel::setAmoled) }
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.appearance_auto_schedule)) },
                trailingContent = { Switch(checked = prefs.autoScheduleAtSunset, onCheckedChange = viewModel::setAutoSchedule) }
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.appearance_reduce_motion)) },
                trailingContent = { Switch(checked = prefs.reduceMotion, onCheckedChange = viewModel::setReduceMotion) }
            )
        }
    }
}
