package com.spectra.ar.tracking

import com.google.common.truth.Truth.assertThat
import com.spectra.ar.vision.BoundingBox
import com.spectra.ar.vision.DetectedObject
import org.junit.Test

class ObjectTrackerTest {

    private fun detection(label: String, box: BoundingBox, confidence: Float = 0.9f) =
        DetectedObject(trackingId = null, label = label, confidence = confidence, boundingBox = box)

    @Test
    fun `new detection creates a new track`() {
        val tracker = ObjectTracker()
        val tracks = tracker.update(listOf(detection("cup", BoundingBox(0f, 0f, 100f, 100f))))
        assertThat(tracks).hasSize(1)
        assertThat(tracks.first().lastDetection.label).isEqualTo("cup")
    }

    @Test
    fun `overlapping detection in next frame reuses the same track id`() {
        val tracker = ObjectTracker()
        val first = tracker.update(listOf(detection("cup", BoundingBox(0f, 0f, 100f, 100f))))
        val firstId = first.first().trackId

        val second = tracker.update(listOf(detection("cup", BoundingBox(5f, 5f, 105f, 105f))))
        assertThat(second).hasSize(1)
        assertThat(second.first().trackId).isEqualTo(firstId)
    }

    @Test
    fun `track survives a few missed frames then drops after the threshold`() {
        val tracker = ObjectTracker()
        tracker.update(listOf(detection("cup", BoundingBox(0f, 0f, 100f, 100f))))

        // Miss frames one at a time; track should persist until TRACKER_MAX_MISSES_BEFORE_DROP is exceeded.
        var lastResult: List<TrackedObject> = emptyList()
        repeat(com.spectra.ar.core.util.SpectraConstants.TRACKER_MAX_MISSES_BEFORE_DROP) {
            lastResult = tracker.update(emptyList())
        }
        assertThat(lastResult).hasSize(1) // still alive at exactly the threshold

        val afterOneMoreMiss = tracker.update(emptyList())
        assertThat(afterOneMoreMiss).isEmpty()
    }

    @Test
    fun `different label at same location does not merge tracks`() {
        val tracker = ObjectTracker()
        tracker.update(listOf(detection("cup", BoundingBox(0f, 0f, 100f, 100f))))
        val second = tracker.update(listOf(detection("bottle", BoundingBox(0f, 0f, 100f, 100f))))
        // "cup" track ages by one miss, "bottle" gets its own new track — two tracks total.
        assertThat(second).hasSize(2)
    }

    @Test
    fun `reset clears all tracks`() {
        val tracker = ObjectTracker()
        tracker.update(listOf(detection("cup", BoundingBox(0f, 0f, 100f, 100f))))
        tracker.reset()
        val afterReset = tracker.update(emptyList())
        assertThat(afterReset).isEmpty()
    }
}
