package com.spectra.ar.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class ArLens(val id: String, val labelResId: Int) {
    EXPLORE("EXPLORE", com.spectra.ar.R.string.lens_explore),
    TEXT("TEXT", com.spectra.ar.R.string.lens_text),
    OBJECT("OBJECT", com.spectra.ar.R.string.lens_object),
    TRAVEL("TRAVEL", com.spectra.ar.R.string.lens_travel),
    MEMORY("MEMORY", com.spectra.ar.R.string.lens_memory),
    SHOPPING("SHOPPING", com.spectra.ar.R.string.lens_shopping),
    STUDY("STUDY", com.spectra.ar.R.string.lens_study),
    NAVIGATION("NAVIGATION", com.spectra.ar.R.string.lens_navigation),
    CUSTOM("CUSTOM", com.spectra.ar.R.string.lens_custom)
}

@Composable
fun LensSelector(selectedLensId: String, onSelect: (ArLens) -> Unit, modifier: Modifier = Modifier) {
    LazyRow(modifier = modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(ArLens.entries) { lens ->
            FilterChip(
                selected = lens.id == selectedLensId,
                onClick = { onSelect(lens) },
                label = { Text(androidx.compose.ui.res.stringResource(lens.labelResId)) }
            )
        }
    }
}
