package com.spectra.ar.ui.screens.arcamera

import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spectra.ar.ai.AiVisionAssistant
import com.spectra.ar.ai.AiVisionQuery
import com.spectra.ar.analytics.SessionManager
import com.spectra.ar.ar.ArSessionManager
import com.spectra.ar.ar.TrackingState
import com.spectra.ar.data.preferences.AppPreferences
import com.spectra.ar.ocr.OcrEngine
import com.spectra.ar.tracking.ObjectTracker
import com.spectra.ar.tracking.TrackedObject
import com.spectra.ar.vision.ModelPerformanceMonitor
import com.spectra.ar.vision.ObjectDetector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ArCameraUiState(
    val lensId: String = "EXPLORE",
    val trackingState: TrackingState = TrackingState.STOPPED,
    val trackedObjects: List<TrackedObject> = emptyList(),
    val hiddenLowConfidenceCount: Int = 0,
    val debugModeEnabled: Boolean = false,
    val isAiThinking: Boolean = false,
    val aiAnswer: String? = null
)

@HiltViewModel
class ArCameraViewModel @Inject constructor(
    private val arSessionManager: ArSessionManager,
    private val objectDetector: ObjectDetector,
    private val ocrEngine: OcrEngine,
    private val objectTracker: ObjectTracker,
    private val performanceMonitor: ModelPerformanceMonitor,
    private val sessionManager: SessionManager,
    private val aiVisionAssistant: AiVisionAssistant,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArCameraUiState())
    val uiState: StateFlow<ArCameraUiState> = _uiState.asStateFlow()

    val performanceSnapshot = performanceMonitor.snapshot

    val debugModeEnabled = appPreferences.debugModeEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        viewModelScope.launch {
            arSessionManager.trackingState.collect { state ->
                _uiState.value = _uiState.value.copy(trackingState = state)
            }
        }
    }

    fun setLens(lensId: String) {
        _uiState.value = _uiState.value.copy(lensId = lensId)
        viewModelScope.launch { sessionManager.startSession(lensId, locationGranted = false, analyticsConsent = false) }
        objectTracker.reset()
    }

    /** Called by the CameraController's frame listener with a decoded, orientation-corrected
     *  bitmap. Runs off the main thread; only UI state mutations are dispatched back. */
    suspend fun onFrameBitmap(bitmap: android.graphics.Bitmap, rotationDegrees: Int, confidenceThreshold: Float) {
        val start = System.currentTimeMillis()
        val detections = objectDetector.detect(bitmap, rotationDegrees, confidenceThreshold)
        performanceMonitor.recordInference(System.currentTimeMillis() - start)
        performanceMonitor.recordFrame()

        val tracked = objectTracker.update(detections)
        detections.forEach { sessionManager.recordObjectDetection() }
        _uiState.value = _uiState.value.copy(trackedObjects = tracked)
    }

    fun askAi(prompt: String, cloudApproved: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAiThinking = true)
            val labels = _uiState.value.trackedObjects.map { it.lastDetection.label }
            val result = aiVisionAssistant.ask(AiVisionQuery(prompt = prompt, detectedObjectLabels = labels), frame = null, userApprovedCloudThisRequest = cloudApproved)
            _uiState.value = _uiState.value.copy(
                isAiThinking = false,
                aiAnswer = result.getOrNull()?.answerText ?: "SPECTRA couldn't answer that right now."
            )
        }
    }
}
