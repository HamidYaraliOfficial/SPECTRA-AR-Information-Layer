package com.spectra.ar.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.spectra.ar.R

@Composable
fun PrivacySettingsScreen(onBack: () -> Unit, viewModel: PrivacySettingsViewModel = hiltViewModel()) {
    val cloudAiAllowed by viewModel.cloudAiAllowed.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_privacy)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null) } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            ListItem(
                headlineContent = { Text(stringResource(R.string.privacy_cloud_ai_toggle)) },
                supportingContent = { Text(stringResource(R.string.privacy_cloud_ai_toggle_desc)) },
                trailingContent = { Switch(checked = cloudAiAllowed, onCheckedChange = viewModel::setCloudAiAllowed) }
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.privacy_data_retention)) },
                supportingContent = { Text(stringResource(R.string.privacy_data_retention_desc)) }
            )
            ListItem(headlineContent = { Text(stringResource(R.string.privacy_export_data)) })
            ListItem(headlineContent = { Text(stringResource(R.string.privacy_delete_all_data)) })
        }
    }
}
