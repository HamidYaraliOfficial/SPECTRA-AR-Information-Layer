package com.spectra.ar.ui.screens.openinghours

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.spectra.ar.R
import com.spectra.ar.openinghours.DayHours
import com.spectra.ar.ui.components.OpeningHoursBadge

private val dayLabelRes = mapOf(
    1 to R.string.hours_day_1, 2 to R.string.hours_day_2, 3 to R.string.hours_day_3,
    4 to R.string.hours_day_4, 5 to R.string.hours_day_5, 6 to R.string.hours_day_6, 7 to R.string.hours_day_7
)

/**
 * Fully user-driven opening-hours editor: every day/time range on this screen is typed in
 * by the person using SPECTRA — nothing here is fetched from an external "business hours"
 * source. The badge at the top updates live via [OpeningHoursCalculator] as they edit.
 */
@Composable
fun OpeningHoursEditorScreen(ownerId: String, onDone: () -> Unit, viewModel: OpeningHoursEditorViewModel = hiltViewModel()) {
    LaunchedEffect(ownerId) { viewModel.load(ownerId) }
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.hours_title)) }) },
        bottomBar = {
            Button(
                onClick = { viewModel.save(); onDone() },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) { Text(stringResource(R.string.hours_save)) }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Text(
                stringResource(R.string.hours_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )

            state.livePreview?.let { snapshot ->
                OpeningHoursBadge(snapshot, state.previewRemaining, modifier = Modifier.padding(horizontal = 16.dp))
            }

            LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 12.dp)) {
                items(state.hours.days) { day ->
                    DayEditorRow(
                        day = day,
                        onClosedChange = { viewModel.setClosed(day.dayOfWeek, it) },
                        onOpen24Change = { viewModel.setOpen24Hours(day.dayOfWeek, it) },
                        onAddRange = { open, close -> viewModel.addRange(day.dayOfWeek, open, close) },
                        onRemoveRange = { index -> viewModel.removeRange(day.dayOfWeek, index) },
                        onCopyToAll = { viewModel.copyDayToAll(day.dayOfWeek) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DayEditorRow(
    day: DayHours,
    onClosedChange: (Boolean) -> Unit,
    onOpen24Change: (Boolean) -> Unit,
    onAddRange: (Int, Int) -> Unit,
    onRemoveRange: (Int) -> Unit,
    onCopyToAll: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(dayLabelRes[day.dayOfWeek] ?: R.string.hours_day_1), style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
            Text(stringResource(R.string.hours_closed_all_day), style = MaterialTheme.typography.labelSmall)
            Checkbox(checked = day.isClosed, onCheckedChange = onClosedChange)
            Text(stringResource(R.string.hours_open_24h), style = MaterialTheme.typography.labelSmall)
            Switch(checked = day.isOpen24Hours, onCheckedChange = onOpen24Change)
        }

        if (!day.isClosed && !day.isOpen24Hours) {
            day.ranges.forEachIndexed { index, range ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${formatMinutes(range.openMinutes)} – ${formatMinutes(range.closeMinutes)}", modifier = Modifier.weight(1f))
                    IconButton(onClick = { onRemoveRange(index) }) { Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.hours_remove_range)) }
                }
            }
            TextButton(onClick = { onAddRange(9 * 60, 18 * 60) }) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Text(stringResource(R.string.hours_add_range))
            }
        }

        TextButton(onClick = onCopyToAll) { Text(stringResource(R.string.hours_copy_to_all_days)) }
    }
}

private fun formatMinutes(totalMinutes: Int): String {
    val normalized = totalMinutes % 1440
    val hours = normalized / 60
    val minutes = normalized % 60
    return "%02d:%02d".format(hours, minutes)
}
