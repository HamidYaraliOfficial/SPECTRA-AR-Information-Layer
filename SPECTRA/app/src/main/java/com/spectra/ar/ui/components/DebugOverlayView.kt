package com.spectra.ar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.spectra.ar.R
import com.spectra.ar.ar.TrackingState
import com.spectra.ar.vision.ModelPerformanceMonitor

/** Developer-only diagnostic overlay (see Settings > Advanced > Debug Mode). Never shown
 *  to a regular user by default — gated by AppPreferences.debugModeEnabled. */
@Composable
fun DebugOverlayView(
    trackingState: TrackingState,
    performance: com.spectra.ar.vision.PerformanceSnapshot,
    trackedPlaneCount: Int,
    anchorCount: Int,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Column {
            Text(stringResource(R.string.hud_debug_mode), style = MaterialTheme.typography.titleSmall)
            Text(stringResource(R.string.debug_tracking_state, trackingState.name), style = MaterialTheme.typography.labelSmall)
            Text("FPS: ${performance.fps}", style = MaterialTheme.typography.labelSmall)
            Text("Inference: ${performance.lastInferenceMs}ms (avg ${performance.averageInferenceMs}ms)", style = MaterialTheme.typography.labelSmall)
            Text("${stringResource(R.string.debug_planes)}: $trackedPlaneCount", style = MaterialTheme.typography.labelSmall)
            Text("${stringResource(R.string.debug_anchors)}: $anchorCount", style = MaterialTheme.typography.labelSmall)
        }
    }
}
