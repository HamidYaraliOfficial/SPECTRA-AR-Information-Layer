package com.spectra.ar.tracking

import com.spectra.ar.vision.DetectedObject

data class TrackedObject(
    val trackId: Int,
    var lastDetection: DetectedObject,
    var missedFrames: Int = 0,
    /** Exponentially-smoothed box so overlays don't jitter frame to frame. */
    var smoothedBox: com.spectra.ar.vision.BoundingBox = lastDetection.boundingBox
)
