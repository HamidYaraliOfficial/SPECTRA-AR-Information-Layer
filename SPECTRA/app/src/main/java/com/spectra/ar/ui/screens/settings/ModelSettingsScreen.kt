package com.spectra.ar.ui.screens.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.spectra.ar.R
import com.spectra.ar.models.ModelInstallState

@Composable
fun ModelSettingsScreen(onBack: () -> Unit, viewModel: ModelSettingsViewModel = hiltViewModel()) {
    val catalog by viewModel.catalog.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_models)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null) } }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(catalog) { model ->
                ListItem(
                    headlineContent = { Text(model.displayName) },
                    supportingContent = { Text("${model.version} · ${model.sizeBytes / (1024 * 1024)} MB") },
                    trailingContent = {
                        when (model.installState) {
                            ModelInstallState.NOT_INSTALLED, ModelInstallState.FAILED ->
                                Button(onClick = { viewModel.download(model.id) }) { Text(stringResource(R.string.models_download)) }
                            ModelInstallState.INSTALLED ->
                                Button(onClick = { viewModel.remove(model) }) { Text(stringResource(R.string.models_remove)) }
                            ModelInstallState.DOWNLOADING, ModelInstallState.VERIFYING ->
                                Text(stringResource(R.string.models_verifying))
                        }
                    }
                )
            }
        }
    }
}
