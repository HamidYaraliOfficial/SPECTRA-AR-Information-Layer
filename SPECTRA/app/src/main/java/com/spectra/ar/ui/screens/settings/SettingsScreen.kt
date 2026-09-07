package com.spectra.ar.ui.screens.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.spectra.ar.R

private data class SettingsRow(val titleRes: Int, val onClick: () -> Unit)

@Composable
fun SettingsScreen(
    onNavigateAppearance: () -> Unit,
    onNavigatePrivacy: () -> Unit,
    onNavigateModels: () -> Unit,
    onBack: () -> Unit
) {
    val rows = listOf(
        SettingsRow(R.string.settings_appearance, onNavigateAppearance),
        SettingsRow(R.string.settings_privacy, onNavigatePrivacy),
        SettingsRow(R.string.settings_models, onNavigateModels)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null) } }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().let { it }) {
            items(rows) { row ->
                ListItem(headlineContent = { Text(stringResource(row.titleRes)) }, modifier = Modifier.clickableCompat(row.onClick))
            }
        }
    }
}

private fun Modifier.clickableCompat(onClick: () -> Unit) =
    this.then(androidx.compose.foundation.clickable(onClick = onClick))
