package com.spectra.ar.spatial

import com.google.ar.core.Frame
import com.spectra.ar.ar.PlaneDetectionManager

/** Aggregates plane/ground/wall understanding for a given frame; the AR overlay layer
 *  queries this once per frame instead of talking to ARCore trackables directly. */
class WorldUnderstandingLayer(
    private val planeDetectionManager: PlaneDetectionManager
) {
    data class WorldSnapshot(
        val horizontalPlaneCount: Int,
        val verticalPlaneCount: Int,
        val hasGroundPlane: Boolean
    )

    fun snapshot(frame: Frame): WorldSnapshot {
        val planes = planeDetectionManager.trackedPlanes(frame)
        val horizontal = planes.count { it.type == com.google.ar.core.Plane.Type.HORIZONTAL_UPWARD_FACING }
        val vertical = planes.count { it.type == com.google.ar.core.Plane.Type.VERTICAL }
        return WorldSnapshot(
            horizontalPlaneCount = horizontal,
            verticalPlaneCount = vertical,
            hasGroundPlane = horizontal > 0
        )
    }
}
