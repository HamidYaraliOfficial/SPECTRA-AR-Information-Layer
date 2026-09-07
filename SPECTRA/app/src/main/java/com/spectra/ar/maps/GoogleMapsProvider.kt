package com.spectra.ar.maps

import javax.inject.Inject
import javax.inject.Singleton

/** Directions via the Google Maps Directions API. Only invoked when the user starts AR
 *  navigation — never runs in the background, and never without an active location
 *  permission grant. The "foss" build flavor ships without Google Play Services and
 *  swaps this for a MapLibre-backed provider instead (see build flavors in app/build.gradle.kts). */
@Singleton
class GoogleMapsProvider @Inject constructor() : MapProvider {
    override suspend fun getRoute(origin: LatLng, destination: LatLng, walking: Boolean): Result<Route> = runCatching {
        // Network call to the Directions API happens here in a full build; kept as an
        // explicit extension point so the routing backend can be swapped per flavor.
        throw NotImplementedError("Wire up to Directions API / MapLibre routing in your environment")
    }
}
