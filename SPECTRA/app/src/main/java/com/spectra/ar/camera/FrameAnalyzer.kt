package com.spectra.ar.camera

import androidx.camera.core.ImageProxy
import androidx.camera.core.ImageAnalysis
import com.spectra.ar.power.PowerAwareEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

data class CameraFrame(
    val image: ImageProxy,
    val rotationDegrees: Int,
    val timestampNanos: Long
)

/**
 * Backpressure-aware [ImageAnalysis.Analyzer]. CameraX is configured with
 * STRATEGY_KEEP_ONLY_LATEST so old frames are dropped at the source; this analyzer
 * additionally gates analysis frequency via [BackpressureController] so a burst of
 * frames on a fast camera doesn't overload the on-device models on a slow SoC.
 *
 * Consumers subscribe via [onFrame]; each frame MUST be closed exactly once by the
 * consumer (heavy work should run off this thread — see ObjectDetector/OcrEngine).
 */
class FrameAnalyzer @Inject constructor(
    private val backpressureController: BackpressureController,
    private val powerAwareEngine: PowerAwareEngine
) : ImageAnalysis.Analyzer {

    private val processingMutex = Mutex()
    private var onFrame: ((CameraFrame) -> Unit)? = null

    fun setFrameListener(listener: (CameraFrame) -> Unit) {
        onFrame = listener
    }

    override fun analyze(image: ImageProxy) {
        val now = System.nanoTime()
        val powerState = powerAwareEngine.currentState.value

        if (processingMutex.isLocked || !backpressureController.shouldAnalyze(now, powerState)) {
            image.close()
            return
        }

        val frame = CameraFrame(image, image.imageInfo.rotationDegrees, now)
        val listener = onFrame
        if (listener == null) {
            image.close()
            return
        }
        // The listener (vision/OCR pipeline) is responsible for closing `image` once done;
        // this keeps analysis fully off the CameraX callback thread.
        listener(frame)
    }
}
