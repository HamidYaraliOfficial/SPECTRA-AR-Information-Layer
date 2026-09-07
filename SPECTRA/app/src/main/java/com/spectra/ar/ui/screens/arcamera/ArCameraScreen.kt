package com.spectra.ar.ui.screens.arcamera

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IconButton
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.camera.view.PreviewView
import androidx.hilt.navigation.compose.hiltViewModel
import com.spectra.ar.R
import com.spectra.ar.ar.TrackingState
import com.spectra.ar.ui.components.ArLens
import com.spectra.ar.ui.components.DebugOverlayView
import com.spectra.ar.ui.components.HudOverlay
import com.spectra.ar.ui.components.LensSelector

/**
 * The AR Camera screen: CameraX preview underneath, HUD/lens selector/object cards drawn
 * on top as Compose overlays positioned via [com.spectra.ar.ar.PoseUtils] projection.
 * Camera bind/frame-analysis wiring lives in a LaunchedEffect against CameraController
 * (omitted from this excerpt for brevity — see camera/CameraController.kt for the pipeline
 * this screen drives).
 */
@Composable
fun ArCameraScreen(lensId: String?, onBack: () -> Unit, viewModel: ArCameraViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val performance by viewModel.performanceSnapshot.collectAsState()
    val debugEnabled by viewModel.debugModeEnabled.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context -> PreviewView(context) },
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface) }
                HudOverlay(
                    trackingState = uiState.trackingState,
                    locationEnabled = false,
                    isOffline = false,
                    fps = performance.fps.takeIf { debugEnabled },
                    hiddenLowConfidenceCount = uiState.hiddenLowConfidenceCount,
                    modifier = Modifier.weight(1f)
                )
            }

            LensSelector(selectedLensId = uiState.lensId, onSelect = { lens -> viewModel.setLens(lens.id) })

            if (uiState.trackingState != TrackingState.TRACKING) {
                Text(
                    text = when (uiState.trackingState) {
                        TrackingState.STOPPED -> stringResource(R.string.hud_unsupported_device_body)
                        else -> stringResource(R.string.hud_tracking_lost)
                    },
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (debugEnabled) {
                DebugOverlayView(
                    trackingState = uiState.trackingState,
                    performance = performance,
                    trackedPlaneCount = 0,
                    anchorCount = 0,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        Row(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            FloatingActionButton(onClick = { /* AR Capture System: snapshot with overlay burned in */ }) {
                Icon(Icons.Filled.CameraAlt, contentDescription = stringResource(R.string.hud_capture))
            }
            FloatingActionButton(onClick = { /* Toggle Context Layers sheet */ }) {
                Icon(Icons.Filled.Layers, contentDescription = stringResource(R.string.hud_layers))
            }
        }
    }
}
