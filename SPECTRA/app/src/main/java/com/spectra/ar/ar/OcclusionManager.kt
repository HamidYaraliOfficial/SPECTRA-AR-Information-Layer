package com.spectra.ar.ar

import com.google.ar.core.Frame
import com.google.ar.core.Session

/**
 * Depth-Aware Occlusion: when the device/session supports the ARCore depth API,
 * SPECTRA reads the depth image to decide, per overlay anchor, whether a real-world
 * object is currently in front of it — and if so fades/hides the overlay instead of
 * drawing it "floating" on top of something that should occlude it.
 */
class OcclusionManager {

    fun isDepthSupported(session: Session): Boolean =
        runCatching { session.isDepthModeSupported(com.google.ar.core.Config.DepthMode.AUTOMATIC) }.getOrDefault(false)

    /** Returns true if the overlay at [anchorDepthMeters] should be occluded, i.e. the
     *  measured scene depth at that screen point is nearer than the anchor itself. */
    fun shouldOcclude(frame: Frame, screenX: Float, screenY: Float, anchorDepthMeters: Float): Boolean {
        val depthImage = runCatching { frame.acquireDepthImage16Bits() }.getOrNull() ?: return false
        depthImage.use { image ->
            val plane = image.planes[0]
            val px = (screenX / frame.camera.let { 1f }).toInt().coerceIn(0, image.width - 1)
            val py = screenY.toInt().coerceIn(0, image.height - 1)
            val byteIndex = py * plane.rowStride + px * plane.pixelStride
            if (byteIndex + 1 >= plane.buffer.remaining()) return false
            val millimeters = (plane.buffer.get(byteIndex).toInt() and 0xFF) or
                ((plane.buffer.get(byteIndex + 1).toInt() and 0xFF) shl 8)
            if (millimeters <= 0) return false
            val measuredMeters = millimeters / 1000f
            return measuredMeters < anchorDepthMeters - OCCLUSION_MARGIN_METERS
        }
    }

    companion object {
        private const val OCCLUSION_MARGIN_METERS = 0.08f
    }
}
