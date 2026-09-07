package com.spectra.ar.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.spectra.ar.R
import com.spectra.ar.ar.TrackingState

/** The always-on status strip at the top of the AR Camera screen: lens, tracking state,
 *  location/offline indicators, and (debug builds only) FPS. */
@Composable
fun HudOverlay(
    trackingState: TrackingState,
    locationEnabled: Boolean,
    isOffline: Boolean,
    fps: Int?,
    hiddenLowConfidenceCount: Int,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(trackingStateLabel(trackingState), style = MaterialTheme.typography.labelMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isOffline) Icon(Icons.Filled.WifiOff, contentDescription = stringResource(R.string.hud_offline), modifier = Modifier.padding(end = 6.dp))
                    Icon(
                        if (locationEnabled) Icons.Filled.LocationOn else Icons.Filled.LocationOff,
                        contentDescription = stringResource(if (locationEnabled) R.string.hud_location_on else R.string.hud_location_off)
                    )
                    fps?.let {
                        Text(stringResource(R.string.hud_fps, it), style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
            if (hiddenLowConfidenceCount > 0) {
                Text(
                    stringResource(R.string.hud_low_confidence_hidden, hiddenLowConfidenceCount),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun trackingStateLabel(state: TrackingState): String = when (state) {
    TrackingState.TRACKING -> stringResource(R.string.hud_scanning)
    TrackingState.PAUSED_INITIALIZING -> stringResource(R.string.hud_relocalizing)
    TrackingState.PAUSED_INSUFFICIENT_FEATURES, TrackingState.PAUSED_EXCESSIVE_MOTION -> stringResource(R.string.hud_tracking_lost)
    TrackingState.STOPPED -> stringResource(R.string.hud_tracking_lost)
}
