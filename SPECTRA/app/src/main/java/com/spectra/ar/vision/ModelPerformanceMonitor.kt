package com.spectra.ar.vision

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class PerformanceSnapshot(val fps: Int = 0, val lastInferenceMs: Long = 0, val averageInferenceMs: Long = 0)

/** Measures inference time and effective FPS so the vision pipeline can adapt quality
 *  on slower devices (see ObjectDetectionSettings in Settings > Vision). */
@Singleton
class ModelPerformanceMonitor @Inject constructor() {
    private val _snapshot = MutableStateFlow(PerformanceSnapshot())
    val snapshot: StateFlow<PerformanceSnapshot> = _snapshot.asStateFlow()

    private val recentInferenceTimesMs = ArrayDeque<Long>(SAMPLE_WINDOW)
    private var lastFrameTimestampNanos = 0L
    private var frameCountInWindow = 0
    private var windowStartNanos = 0L

    fun recordInference(durationMs: Long) {
        recentInferenceTimesMs.addLast(durationMs)
        if (recentInferenceTimesMs.size > SAMPLE_WINDOW) recentInferenceTimesMs.removeFirst()
        val avg = recentInferenceTimesMs.average().toLong()
        _snapshot.value = _snapshot.value.copy(lastInferenceMs = durationMs, averageInferenceMs = avg)
    }

    fun recordFrame() {
        val now = System.nanoTime()
        if (windowStartNanos == 0L) windowStartNanos = now
        frameCountInWindow++
        val elapsedSec = (now - windowStartNanos) / 1_000_000_000.0
        if (elapsedSec >= 1.0) {
            _snapshot.value = _snapshot.value.copy(fps = (frameCountInWindow / elapsedSec).toInt())
            frameCountInWindow = 0
            windowStartNanos = now
        }
    }

    companion object { private const val SAMPLE_WINDOW = 30 }
}
