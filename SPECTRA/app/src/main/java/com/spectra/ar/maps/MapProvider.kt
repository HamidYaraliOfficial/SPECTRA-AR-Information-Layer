package com.spectra.ar.maps

data class LatLng(val latitude: Double, val longitude: Double)
data class RouteStep(val instruction: String, val distanceMeters: Int, val point: LatLng)
data class Route(val steps: List<RouteStep>, val totalDistanceMeters: Int, val estimatedDurationSeconds: Int)

interface MapProvider {
    suspend fun getRoute(origin: LatLng, destination: LatLng, walking: Boolean = true): Result<Route>
}
