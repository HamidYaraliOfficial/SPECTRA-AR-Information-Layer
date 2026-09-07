package com.spectra.ar.core.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/** Central place that enumerates every permission SPECTRA can ever request and why. */
enum class SpectraPermission(val manifestPermission: String, val isCore: Boolean) {
    CAMERA(Manifest.permission.CAMERA, isCore = true),
    LOCATION_FINE(Manifest.permission.ACCESS_FINE_LOCATION, isCore = false),
    LOCATION_COARSE(Manifest.permission.ACCESS_COARSE_LOCATION, isCore = false),
    MICROPHONE(Manifest.permission.RECORD_AUDIO, isCore = false),
    NOTIFICATIONS(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.POST_NOTIFICATIONS else "",
        isCore = false
    );

    fun isGranted(context: Context): Boolean {
        if (manifestPermission.isEmpty()) return true // Not required below API level
        return ContextCompat.checkSelfPermission(context, manifestPermission) == PackageManager.PERMISSION_GRANTED
    }
}
