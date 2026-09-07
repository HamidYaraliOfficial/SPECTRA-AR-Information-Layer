package com.spectra.ar.tracking

import com.spectra.ar.core.util.SpectraConstants
import com.spectra.ar.vision.BoundingBox
import com.spectra.ar.vision.DetectedObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Lightweight IOU-based multi-object tracker. Runs on top of per-frame detections
 * (which may themselves come at a reduced cadence — see BackpressureController) to keep
 * overlays visually stable between detection passes and to survive brief misses without
 * the overlay disappearing and reappearing.
 */
@Singleton
class ObjectTracker @Inject constructor() {

    private var nextTrackId = 1
    private val tracked = mutableMapOf<Int, TrackedObject>()

    fun update(detections: List<DetectedObject>): List<TrackedObject> {
        val unmatchedDetections = detections.toMutableList()
        val matchedTrackIds = mutableSetOf<Int>()

        for (track in tracked.values) {
            val bestMatch = unmatchedDetections
                .filter { it.label == track.lastDetection.label }
                .maxByOrNull { it.boundingBox.iou(track.smoothedBox) }

            val iou = bestMatch?.boundingBox?.iou(track.smoothedBox) ?: 0f
            if (bestMatch != null && iou >= SpectraConstants.TRACKER_IOU_MATCH_THRESHOLD) {
                track.lastDetection = bestMatch
                track.smoothedBox = smooth(track.smoothedBox, bestMatch.boundingBox)
                track.missedFrames = 0
                matchedTrackIds += track.trackId
                unmatchedDetections.remove(bestMatch)
            } else {
                track.missedFrames++
            }
        }

        tracked.entries.removeAll { it.value.missedFrames > SpectraConstants.TRACKER_MAX_MISSES_BEFORE_DROP }

        for (detection in unmatchedDetections) {
            val id = nextTrackId++
            tracked[id] = TrackedObject(trackId = id, lastDetection = detection, smoothedBox = detection.boundingBox)
        }

        return tracked.values.toList()
    }

    fun reset() {
        tracked.clear()
        nextTrackId = 1
    }

    private fun smooth(previous: BoundingBox, current: BoundingBox, alpha: Float = 0.55f): BoundingBox = BoundingBox(
        left = lerp(previous.left, current.left, alpha),
        top = lerp(previous.top, current.top, alpha),
        right = lerp(previous.right, current.right, alpha),
        bottom = lerp(previous.bottom, current.bottom, alpha)
    )

    private fun lerp(a: Float, b: Float, t: Float) = a + (b - a) * t
}
