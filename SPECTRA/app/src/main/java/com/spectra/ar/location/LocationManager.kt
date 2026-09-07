package com.spectra.ar.location

import android.annotation.SuppressLint
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

data class SpectraLocation(val latitude: Double, val longitude: Double, val accuracyMeters: Float, val bearingDegrees: Float?)

/** Thin, permission-gated wrapper over the Fused Location Provider. Location is entirely
 *  optional in SPECTRA — every consumer must handle a null/absent stream gracefully. */
@Singleton
class SpectraLocationManager @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient
) {
    @SuppressLint("MissingPermission") // Caller is required to have checked SpectraPermission.LOCATION_* first
    fun observeLocation(intervalMillis: Long = 3000L): Flow<SpectraLocation> = callbackFlow {
        val request = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, intervalMillis).build()
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let {
                    trySend(SpectraLocation(it.latitude, it.longitude, it.accuracy, if (it.hasBearing()) it.bearing else null))
                }
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(request, callback, null)
        awaitClose { fusedLocationProviderClient.removeLocationUpdates(callback) }
    }
}
