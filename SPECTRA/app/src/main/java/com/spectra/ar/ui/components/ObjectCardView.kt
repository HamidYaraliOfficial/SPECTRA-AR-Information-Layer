package com.spectra.ar.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.spectra.ar.R
import com.spectra.ar.openinghours.OpeningHoursSnapshot
import com.spectra.ar.openinghours.RemainingDuration
import com.spectra.ar.vision.DetectedObject

/** The floating AR card attached to a detected/tracked object, with the standard
 *  Pin / Save / Search / Share / Note / Task / Translate / Hide action row. */
@Composable
fun ObjectCardView(
    detectedObject: DetectedObject,
    explanation: String,
    hoursSnapshot: OpeningHoursSnapshot? = null,
    hoursRemaining: RemainingDuration? = null,
    onSave: () -> Unit,
    onSearch: () -> Unit,
    onShare: () -> Unit,
    onNote: () -> Unit,
    onTranslate: () -> Unit,
    onHide: () -> Unit
) {
    GlassCard(modifier = Modifier.widthIn(max = 260.dp)) {
        Column {
            Text(detectedObject.label.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.titleSmall)
            Text(explanation, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                stringResource(R.string.object_card_confidence, (detectedObject.confidence * 100).toInt()),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            hoursSnapshot?.let {
                Spacer(modifier = Modifier.width(6.dp))
                OpeningHoursBadge(it, hoursRemaining)
            }
            Row {
                IconButton(onClick = onSave) { Icon(Icons.Filled.Bookmark, contentDescription = stringResource(R.string.object_card_save)) }
                IconButton(onClick = onSearch) { Icon(Icons.Filled.Search, contentDescription = stringResource(R.string.object_card_search)) }
                IconButton(onClick = onShare) { Icon(Icons.Filled.Share, contentDescription = stringResource(R.string.object_card_share)) }
                IconButton(onClick = onNote) { Icon(Icons.Filled.Note, contentDescription = stringResource(R.string.object_card_note)) }
                IconButton(onClick = onTranslate) { Icon(Icons.Filled.Translate, contentDescription = stringResource(R.string.object_card_translate)) }
                IconButton(onClick = onHide) { Icon(Icons.Filled.VisibilityOff, contentDescription = stringResource(R.string.object_card_hide)) }
            }
        }
    }
}
