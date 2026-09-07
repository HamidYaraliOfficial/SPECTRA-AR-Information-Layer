package com.spectra.ar.vision

import android.graphics.Bitmap

/** Pluggable backend contract so a downloaded custom model (TFLite or ONNX) can be swapped
 *  in for a specific Lens (e.g. a "Study Lens" model fine-tuned on textbook diagrams)
 *  without touching the rest of the vision pipeline. */
interface CustomModelBackend {
    val modelId: String
    fun loadFromFile(path: String)
    fun infer(bitmap: Bitmap, confidenceThreshold: Float): List<DetectedObject>
    fun close()
}
