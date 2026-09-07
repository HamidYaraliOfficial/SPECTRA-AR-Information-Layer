package com.spectra.ar.camera

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import com.spectra.ar.core.util.SpectraResult
import kotlinx.coroutines.guava.await
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Owns the CameraX use-case graph: Preview + ImageAnalysis (feeding the vision/OCR
 * pipeline) + ImageCapture + VideoCapture (AR Capture System). All heavy processing
 * happens on a dedicated single-thread executor, off the main thread.
 */
@Singleton
class CameraController @Inject constructor(
    private val frameAnalyzer: FrameAnalyzer
) {
    private val analysisExecutor = Executors.newSingleThreadExecutor()
    private var cameraProvider: ProcessCameraProvider? = null

    var imageCapture: ImageCapture? = null
        private set
    var videoCapture: VideoCapture<Recorder>? = null
        private set

    suspend fun start(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView
    ): SpectraResult<Unit> = try {
        val provider = ProcessCameraProvider.getInstance(context).await()
        cameraProvider = provider
        provider.unbindAll()

        val preview = Preview.Builder().build().also { it.surfaceProvider = previewView.surfaceProvider }

        val analysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also { it.setAnalyzer(analysisExecutor, frameAnalyzer) }

        val capture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

        val recorder = Recorder.Builder().build()
        val video = VideoCapture.withOutput(recorder)

        provider.bindToLifecycle(
            lifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            preview, analysis, capture, video
        )

        imageCapture = capture
        videoCapture = video
        SpectraResult.Success(Unit)
    } catch (t: Throwable) {
        SpectraResult.Error(t, "Failed to start camera")
    }

    fun stop() {
        cameraProvider?.unbindAll()
        cameraProvider = null
    }

    fun onFrame(listener: (CameraFrame) -> Unit) {
        frameAnalyzer.setFrameListener(listener)
    }

    fun shutdown() {
        stop()
        analysisExecutor.shutdown()
    }
}
