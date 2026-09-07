package com.spectra.ar.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Fluent/Windows 11–inspired color tokens.
 * Each accent defines a light and dark seed pair; Material 3 color schemes
 * are built from these in [Theme.kt].
 */

// Windows Default accent — Fluent Blue (#0078D4)
val WinDefaultLightPrimary = Color(0xFF0067C0)
val WinDefaultLightOnPrimary = Color(0xFFFFFFFF)
val WinDefaultDarkPrimary = Color(0xFF60CDFF)
val WinDefaultDarkOnPrimary = Color(0xFF00344B)

// Blue accent — deeper Fluent Navy Blue
val BlueLightPrimary = Color(0xFF2B5FCE)
val BlueLightOnPrimary = Color(0xFFFFFFFF)
val BlueDarkPrimary = Color(0xFFAEC6FF)
val BlueDarkOnPrimary = Color(0xFF002E77)

// Red accent — Fluent Red
val RedLightPrimary = Color(0xFFC42B1C)
val RedLightOnPrimary = Color(0xFFFFFFFF)
val RedDarkPrimary = Color(0xFFFFB4A9)
val RedDarkOnPrimary = Color(0xFF690003)

// Neutral Fluent surfaces (Mica-inspired translucent layering)
val MicaLightSurface = Color(0xFFF3F3F3)
val MicaLightSurfaceVariant = Color(0xFFE7E7E7)
val MicaDarkSurface = Color(0xFF202020)
val MicaDarkSurfaceVariant = Color(0xFF2C2C2C)
val AmoledSurface = Color(0xFF000000)

// Semantic AR overlay colors (used directly by the HUD/renderer, outside the Material scheme)
val ArConfidenceHigh = Color(0xFF3FB950)
val ArConfidenceMedium = Color(0xFFE3A008)
val ArConfidenceLow = Color(0xFF8A8A8A)
val ArTrackingLost = Color(0xFFE5484D)
val ArAnchorGlow = Color(0xFF60CDFF)
val HoursOpenGreen = Color(0xFF3FB950)
val HoursClosedRed = Color(0xFFE5484D)
val HoursClosingSoonAmber = Color(0xFFE3A008)
