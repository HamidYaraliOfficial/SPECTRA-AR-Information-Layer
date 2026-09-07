package com.spectra.ar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.spectra.ar.R
import com.spectra.ar.openinghours.OpenStatus
import com.spectra.ar.openinghours.OpeningHoursSnapshot
import com.spectra.ar.openinghours.RemainingDuration
import com.spectra.ar.ui.theme.HoursClosedRed
import com.spectra.ar.ui.theme.HoursClosingSoonAmber
import com.spectra.ar.ui.theme.HoursOpenGreen

/** Live "open now / closes in 12m" badge shown on Object/Place cards, driven entirely by
 *  user-entered [OpeningHoursSnapshot] — see openinghours/OpeningHoursCalculator. */
@Composable
fun OpeningHoursBadge(snapshot: OpeningHoursSnapshot, remaining: RemainingDuration?, modifier: Modifier = Modifier) {
    val (dotColor, label) = when (snapshot.status) {
        OpenStatus.OPEN -> HoursOpenGreen to stringResource(R.string.hours_open_now)
        OpenStatus.CLOSING_SOON -> HoursClosingSoonAmber to remaining?.let {
            stringResource(R.string.hours_closes_in, formatDuration(it))
        }.orDefault(stringResource(R.string.hours_closing_soon))
        OpenStatus.OPENING_SOON -> HoursClosingSoonAmber to remaining?.let {
            stringResource(R.string.hours_opens_in, formatDuration(it))
        }.orDefault(stringResource(R.string.hours_closed_now))
        OpenStatus.CLOSED -> HoursClosedRed to stringResource(R.string.hours_closed_now)
        OpenStatus.UNKNOWN -> MaterialTheme.colorScheme.outline to stringResource(R.string.hours_unknown)
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.size(8.dp).clip(CircleShape).background(dotColor)
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(6.dp))
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun formatDuration(d: RemainingDuration): String = when {
    d.days > 0 -> stringResource(R.string.hours_duration_days, d.days, d.hours)
    d.hours > 0 -> stringResource(R.string.hours_duration_hours, d.hours, d.minutes)
    else -> stringResource(R.string.hours_duration_minutes, d.minutes)
}

private fun String?.orDefault(fallback: String) = this ?: fallback
