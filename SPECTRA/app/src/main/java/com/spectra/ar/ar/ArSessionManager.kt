package com.spectra.ar.ar

import android.app.Activity
import android.content.Context
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Session
import com.google.ar.core.exceptions.CameraNotAvailableException
import com.google.ar.core.exceptions.UnavailableException
import com.spectra.ar.core.util.SpectraResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Owns the ARCore [Session] lifecycle: install checks, create/resume/pause/close,
 * per-frame update, and exposes current tracking state for the HUD and
 * OcclusionManager/AnchorManager to react to (e.g. "tracking lost" banner,
 * re-localization prompts).
 */
@Singleton
class ArSessionManager @Inject constructor() {

    private var session: Session? = null

    private val _trackingState = MutableStateFlow(TrackingState.STOPPED)
    val trackingState: StateFlow<TrackingState> = _trackingState.asStateFlow()

    fun checkAvailability(context: Context): ArAvailability = when (ArCoreApk.getInstance().checkAvailability(context)) {
        ArCoreApk.Availability.SUPPORTED_INSTALLED -> ArAvailability.SUPPORTED_READY
        ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD -> ArAvailability.SUPPORTED_NEEDS_UPDATE
        ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED -> ArAvailability.SUPPORTED_NEEDS_INSTALL
        ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE -> ArAvailability.UNSUPPORTED
        else -> ArAvailability.UNKNOWN
    }

    fun requestInstall(activity: Activity, userRequestedInstall: Boolean): ArCoreApk.InstallStatus =
        ArCoreApk.getInstance().requestInstall(activity, userRequestedInstall)

    fun createSession(context: Context, enableDepth: Boolean, enablePlaneFinding: Boolean): SpectraResult<Session> = try {
        val newSession = Session(context)
        val config = Config(newSession).apply {
            planeFindingMode = if (enablePlaneFinding) Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL else Config.PlaneFindingMode.DISABLED
            focusMode = Config.FocusMode.AUTO
            updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
            lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
            if (enableDepth && newSession.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                depthMode = Config.DepthMode.AUTOMATIC
            }
            cloudAnchorMode = Config.CloudAnchorMode.DISABLED // enabled per-anchor via AnchorManager when the user opts in to sharing
        }
        newSession.configure(config)
        session = newSession
        SpectraResult.Success(newSession)
    } catch (e: UnavailableException) {
        SpectraResult.Error(e, "ARCore session could not be created")
    }

    fun resume(): SpectraResult<Unit> = try {
        session?.resume()
        SpectraResult.Success(Unit)
    } catch (e: CameraNotAvailableException) {
        SpectraResult.Error(e, "Camera not available for AR session")
    }

    fun pause() {
        session?.pause()
    }

    fun close() {
        session?.close()
        session = null
        _trackingState.value = TrackingState.STOPPED
    }

    /** Call once per CameraX frame; returns the latest ARCore [Frame] or null if not tracking. */
    fun update(): Frame? {
        val currentSession = session ?: return null
        val frame = runCatching { currentSession.update() }.getOrNull() ?: return null

        _trackingState.value = when (frame.camera.trackingState) {
            com.google.ar.core.TrackingState.TRACKING -> TrackingState.TRACKING
            com.google.ar.core.TrackingState.PAUSED -> when (frame.camera.trackingFailureReason) {
                com.google.ar.core.TrackingFailureReason.INSUFFICIENT_FEATURES -> TrackingState.PAUSED_INSUFFICIENT_FEATURES
                com.google.ar.core.TrackingFailureReason.EXCESSIVE_MOTION -> TrackingState.PAUSED_EXCESSIVE_MOTION
                else -> TrackingState.PAUSED_INITIALIZING
            }
            else -> TrackingState.STOPPED
        }
        return frame
    }

    fun currentSession(): Session? = session
}
