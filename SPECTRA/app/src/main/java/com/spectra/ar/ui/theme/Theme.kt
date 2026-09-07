package com.spectra.ar.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private fun schemeFor(accent: AccentColor, dark: Boolean) = when (accent) {
    AccentColor.WINDOWS_DEFAULT -> if (dark) {
        darkColorScheme(primary = WinDefaultDarkPrimary, onPrimary = WinDefaultDarkOnPrimary, surface = MicaDarkSurface, surfaceVariant = MicaDarkSurfaceVariant)
    } else {
        lightColorScheme(primary = WinDefaultLightPrimary, onPrimary = WinDefaultLightOnPrimary, surface = MicaLightSurface, surfaceVariant = MicaLightSurfaceVariant)
    }
    AccentColor.BLUE -> if (dark) {
        darkColorScheme(primary = BlueDarkPrimary, onPrimary = BlueDarkOnPrimary, surface = MicaDarkSurface, surfaceVariant = MicaDarkSurfaceVariant)
    } else {
        lightColorScheme(primary = BlueLightPrimary, onPrimary = BlueLightOnPrimary, surface = MicaLightSurface, surfaceVariant = MicaLightSurfaceVariant)
    }
    AccentColor.RED -> if (dark) {
        darkColorScheme(primary = RedDarkPrimary, onPrimary = RedDarkOnPrimary, surface = MicaDarkSurface, surfaceVariant = MicaDarkSurfaceVariant)
    } else {
        lightColorScheme(primary = RedLightPrimary, onPrimary = RedLightOnPrimary, surface = MicaLightSurface, surfaceVariant = MicaLightSurfaceVariant)
    }
}

@Composable
fun SpectraTheme(
    preferences: ThemePreferences = ThemePreferences(),
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val useDark = when (preferences.mode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val context = LocalContext.current

    var colorScheme = when {
        preferences.dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (useDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        else -> schemeFor(preferences.accent, useDark)
    }

    if (useDark && preferences.amoled) {
        colorScheme = colorScheme.copy(surface = AmoledSurface, background = AmoledSurface)
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDark
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SpectraTypography,
        shapes = SpectraShapes,
        content = content
    )
}
