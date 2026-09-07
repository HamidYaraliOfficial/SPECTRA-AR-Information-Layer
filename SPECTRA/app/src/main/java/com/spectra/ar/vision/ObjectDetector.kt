package com.spectra.ar.vision

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Default on-device object detector, backed by ML Kit's Object Detection & Tracking API
 * (STREAM_MODE — built-in multi-frame tracking IDs). Custom models (TFLite/ONNX) plug in
 * through [ModelManager] and are dispatched to by the same interface.
 */
@Singleton
class ObjectDetector @Inject constructor() {

    private val streamDetector by lazy {
        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
            .enableMultipleObjects()
            .enableClassification()
            .build()
        ObjectDetection.getClient(options)
    }

    suspend fun detect(bitmap: Bitmap, rotationDegrees: Int, confidenceThreshold: Float): List<DetectedObject> {
        val image = InputImage.fromBitmap(bitmap, rotationDegrees)
        val results = streamDetector.process(image).await()
        return results.mapNotNull { obj ->
            val bestLabel = obj.labels.maxByOrNull { it.confidence } ?: return@mapNotNull null
            if (bestLabel.confidence < confidenceThreshold) return@mapNotNull null
            DetectedObject(
                trackingId = obj.trackingId,
                label = bestLabel.text,
                confidence = bestLabel.confidence,
                boundingBox = BoundingBox(
                    obj.boundingBox.left.toFloat(), obj.boundingBox.top.toFloat(),
                    obj.boundingBox.right.toFloat(), obj.boundingBox.bottom.toFloat()
                )
            )
        }
    }
}
