package com.spectra.ar.ui.theme

/** Windows 11–inspired theme mode, mirrors Windows Settings > Personalization > Colors. */
enum class ThemeMode { SYSTEM, LIGHT, DARK }

/** Accent palette. WINDOWS_DEFAULT mirrors the Windows 11 default Fluent accent (#0078D4). */
enum class AccentColor { WINDOWS_DEFAULT, BLUE, RED }

data class ThemePreferences(
    val mode: ThemeMode = ThemeMode.SYSTEM,
    val accent: AccentColor = AccentColor.WINDOWS_DEFAULT,
    val dynamicColor: Boolean = false,
    val amoled: Boolean = false,
    val autoScheduleAtSunset: Boolean = false,
    val reduceMotion: Boolean = false
)
