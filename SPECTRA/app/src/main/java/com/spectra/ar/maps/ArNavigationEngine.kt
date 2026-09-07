package com.spectra.ar.maps

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class ArWaypoint(val latLng: LatLng, val distanceMeters: Float, val bearingDegrees: Float, val instruction: String)

/** Turns a [Route] plus the user's live location/heading into AR Arrows + Waypoints
 *  (2D overlay markers projected via [com.spectra.ar.ar.PoseUtils], consistent with the
 *  rest of SPECTRA's lightweight AR rendering approach). */
class ArNavigationEngine {

    fun nextWaypoint(route: Route, currentLocation: LatLng): ArWaypoint? {
        val nextStep = route.steps.firstOrNull() ?: return null
        val distance = haversineMeters(currentLocation, nextStep.point)
        val bearing = bearingDegrees(currentLocation, nextStep.point)
        return ArWaypoint(nextStep.point, distance, bearing, nextStep.instruction)
    }

    private fun haversineMeters(a: LatLng, b: LatLng): Float {
        val earthRadius = 6371000.0
        val dLat = Math.toRadians(b.latitude - a.latitude)
        val dLng = Math.toRadians(b.longitude - a.longitude)
        val lat1 = Math.toRadians(a.latitude)
        val lat2 = Math.toRadians(b.latitude)
        val h = sin(dLat / 2).let { it * it } + cos(lat1) * cos(lat2) * sin(dLng / 2).let { it * it }
        return (earthRadius * 2 * atan2(sqrt(h), sqrt(1 - h))).toFloat()
    }

    private fun bearingDegrees(a: LatLng, b: LatLng): Float {
        val lat1 = Math.toRadians(a.latitude)
        val lat2 = Math.toRadians(b.latitude)
        val dLng = Math.toRadians(b.longitude - a.longitude)
        val y = sin(dLng) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLng)
        return ((Math.toDegrees(atan2(y, x)) + 360) % 360).toFloat()
    }
}
