package com.spectra.ar.vision

data class BoundingBox(val left: Float, val top: Float, val right: Float, val bottom: Float) {
    val width get() = right - left
    val height get() = bottom - top
    val centerX get() = left + width / 2f
    val centerY get() = top + height / 2f

    fun iou(other: BoundingBox): Float {
        val interLeft = maxOf(left, other.left)
        val interTop = maxOf(top, other.top)
        val interRight = minOf(right, other.right)
        val interBottom = minOf(bottom, other.bottom)
        if (interRight <= interLeft || interBottom <= interTop) return 0f
        val interArea = (interRight - interLeft) * (interBottom - interTop)
        val unionArea = width * height + other.width * other.height - interArea
        return if (unionArea <= 0f) 0f else interArea / unionArea
    }
}

data class DetectedObject(
    val trackingId: Int?,
    val label: String,
    val confidence: Float,
    val boundingBox: BoundingBox,
    /** Populated once a plane hit-test / depth sample resolves a 3D position for this box. */
    val spatialPositionKnown: Boolean = false
)
