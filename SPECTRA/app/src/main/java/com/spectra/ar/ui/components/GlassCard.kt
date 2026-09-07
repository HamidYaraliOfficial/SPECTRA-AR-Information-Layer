package com.spectra.ar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

/**
 * Windows 11 "Mica/Acrylic"-inspired translucent surface: a soft gradient over the
 * theme's surface color with a subtle 1dp border, used for HUD panels and floating cards
 * so AR overlays read as part of a coherent shell rather than plain flat rectangles.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Int = 12,
    content: @Composable () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(scheme.surface.copy(alpha = 0.86f), scheme.surface.copy(alpha = 0.72f))
                )
            )
            .border(1.dp, scheme.outline.copy(alpha = 0.15f), RoundedCornerShape(cornerRadius.dp))
            .padding(12.dp)
    ) {
        content()
    }
}
