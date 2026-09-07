package com.spectra.ar.ar

import com.google.ar.core.Camera
import com.google.ar.core.Pose

/** Screen-space projection of an ARCore [Pose], used by the 2D overlay renderer. */
data class ScreenPoint(val x: Float, val y: Float, val depthMeters: Float, val isInFrontOfCamera: Boolean)

object PoseUtils {

    /**
     * Projects a world-space [Pose] to normalized screen coordinates using the camera's
     * current view + projection matrices. This is the basis of SPECTRA's default,
     * lightweight AR overlay: rather than a full 3D scene graph, Compose Cards/Labels/Markers
     * are positioned directly on screen from this projection every frame — cheap, robust
     * across devices, and easy to style with Material 3.
     */
    fun projectToScreen(camera: Camera, pose: Pose, viewportWidth: Int, viewportHeight: Int): ScreenPoint {
        val viewMatrix = FloatArray(16)
        val projMatrix = FloatArray(16)
        camera.getViewMatrix(viewMatrix, 0)
        camera.getProjectionMatrix(projMatrix, 0, NEAR_PLANE, FAR_PLANE)

        val worldPoint = floatArrayOf(pose.tx(), pose.ty(), pose.tz(), 1f)
        val viewPoint = FloatArray(4)
        multiplyMV(viewPoint, viewMatrix, worldPoint)
        val clipPoint = FloatArray(4)
        multiplyMV(clipPoint, projMatrix, viewPoint)

        val inFront = clipPoint[3] > 0f
        if (clipPoint[3] == 0f) return ScreenPoint(0f, 0f, 0f, false)

        val ndcX = clipPoint[0] / clipPoint[3]
        val ndcY = clipPoint[1] / clipPoint[3]
        val screenX = (ndcX * 0.5f + 0.5f) * viewportWidth
        val screenY = (1f - (ndcY * 0.5f + 0.5f)) * viewportHeight
        val depth = -viewPoint[2]

        return ScreenPoint(screenX, screenY, depth, inFront && depth > 0f)
    }

    private fun multiplyMV(out: FloatArray, m: FloatArray, v: FloatArray) {
        for (row in 0 until 4) {
            out[row] = (0 until 4).sumOf { col -> (m[col * 4 + row] * v[col]).toDouble() }.toFloat()
        }
    }

    private const val NEAR_PLANE = 0.05f
    private const val FAR_PLANE = 100f
}
