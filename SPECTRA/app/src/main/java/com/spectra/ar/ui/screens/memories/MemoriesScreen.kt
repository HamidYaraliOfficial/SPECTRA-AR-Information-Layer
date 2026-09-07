package com.spectra.ar.ui.screens.memories

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.spectra.ar.R

@Composable
fun MemoriesScreen(onOpenMarker: (String) -> Unit, viewModel: MemoriesViewModel = hiltViewModel()) {
    val notes by viewModel.notes.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.memories_title)) }) }) { padding ->
        if (notes.isEmpty()) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.memories_empty), style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(notes) { note ->
                    ListItem(
                        headlineContent = { Text(note.body) },
                        supportingContent = { Text(stringResource(R.string.memory_created_at, note.createdAtEpochMillis.toString())) }
                    )
                }
            }
        }
    }
}
