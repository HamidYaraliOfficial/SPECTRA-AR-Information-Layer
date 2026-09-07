package com.spectra.ar.ui.screens.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.spectra.ar.R

@Composable
fun MapScreen() {
    val cameraPositionState = rememberCameraPositionState()

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.map_title)) }) }) { padding ->
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        )
    }
}
