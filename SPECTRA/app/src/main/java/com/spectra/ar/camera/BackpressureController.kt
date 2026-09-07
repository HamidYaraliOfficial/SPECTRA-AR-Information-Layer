package com.spectra.ar.camera

import com.spectra.ar.core.util.SpectraConstants
import com.spectra.ar.power.PowerState
import java.util.concurrent.atomic.AtomicLong

/**
 * Decides whether the next camera frame should actually be run through the heavy
 * Vision/OCR pipeline. Keeps the analysis rate adaptive to device power state so
 * SPECTRA never queues up frames faster than it can process them (CameraX's
 * STRATEGY_KEEP_ONLY_LATEST handles dropping at the source; this adds a minimum
 * time-between-analyses gate on top so we don't saturate a slow device even when
 * frames arrive one at a time).
 */
class BackpressureController {
    private val lastAnalysisAtNanos = AtomicLong(0)

    fun shouldAnalyze(nowNanos: Long, powerState: PowerState): Boolean {
        val minIntervalMs = when {
            powerState.isThermalThrottling || powerState.isBatterySaver -> SpectraConstants.MIN_DETECTION_INTERVAL_MS_BATTERY_SAVER
            powerState.isCharging -> SpectraConstants.MIN_DETECTION_INTERVAL_MS_HIGH_POWER
            else -> SpectraConstants.MIN_DETECTION_INTERVAL_MS_BALANCED
        }
        val elapsedMs = (nowNanos - lastAnalysisAtNanos.get()) / 1_000_000
        if (elapsedMs < minIntervalMs) return false
        lastAnalysisAtNanos.set(nowNanos)
        return true
    }
}
