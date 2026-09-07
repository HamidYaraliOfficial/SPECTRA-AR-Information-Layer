package com.spectra.ar.ar

import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState as ArTrackingState

/** World Understanding Layer: exposes currently tracked horizontal/vertical planes for
 *  hit-testing (marker placement) and for the optional debug-mode plane overlay. */
class PlaneDetectionManager {

    fun trackedPlanes(frame: Frame): List<Plane> =
        frame.getUpdatedTrackables(Plane::class.java)
            .filter { it.trackingState == ArTrackingState.TRACKING && it.subsumedBy == null }
            .toList()

    fun hitTestPlane(frame: Frame, xPx: Float, yPx: Float) =
        frame.hitTest(xPx, yPx).firstOrNull { hit ->
            val trackable = hit.trackable
            trackable is Plane && trackable.isPoseInPolygon(hit.hitPose) && trackable.trackingState == ArTrackingState.TRACKING
        }
}
