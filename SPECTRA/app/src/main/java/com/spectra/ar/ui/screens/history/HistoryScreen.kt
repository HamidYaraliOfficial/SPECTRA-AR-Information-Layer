package com.spectra.ar.ui.screens.history

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
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

@Composable
fun HistoryScreen(viewModel: HistoryViewModel = hiltViewModel()) {
    val detections by viewModel.detections.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.history_title)) },
                actions = { IconButton(onClick = viewModel::clearHistory) { Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.history_clear)) } }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            items(detections) { entry ->
                ListItem(
                    headlineContent = { Text(entry.label) },
                    supportingContent = { Text("${entry.kind} · ${(entry.confidence * 100).toInt()}%") }
                )
            }
        }
    }
}
