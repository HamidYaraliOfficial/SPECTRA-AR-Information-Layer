package com.spectra.ar.vision

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.graphics.Bitmap
import java.nio.FloatBuffer

/** ONNX Runtime Mobile backend — an alternative to TFLite for custom models exported from
 *  PyTorch or other ONNX-compatible toolchains. Same [CustomModelBackend] contract, so
 *  [ModelManager] can hand either backend to the vision pipeline transparently. */
class OnnxModelBackend(override val modelId: String, private val labels: List<String>) : CustomModelBackend {

    private val environment = OrtEnvironment.getEnvironment()
    private var session: OrtSession? = null

    override fun loadFromFile(path: String) {
        session = environment.createSession(path, OrtSession.SessionOptions())
    }

    override fun infer(bitmap: Bitmap, confidenceThreshold: Float): List<DetectedObject> {
        val activeSession = session ?: return emptyList()
        val inputName = activeSession.inputNames.iterator().next()
        val floatBuffer = bitmapToNormalizedFloatBuffer(bitmap)
        val shape = longArrayOf(1, 3, INPUT_SIZE.toLong(), INPUT_SIZE.toLong())

        OnnxTensor.createTensor(environment, floatBuffer, shape).use { tensor ->
            activeSession.run(mapOf(inputName to tensor)).use { results ->
                @Suppress("UNCHECKED_CAST")
                val raw = (results[0].value as Array<FloatArray>)
                return raw.mapNotNull { row ->
                    val score = row.getOrElse(4) { 0f }
                    if (score < confidenceThreshold) return@mapNotNull null
                    val label = labels.getOrElse(row.getOrElse(5) { 0f }.toInt()) { "object" }
                    DetectedObject(
                        trackingId = null,
                        label = label,
                        confidence = score,
                        boundingBox = BoundingBox(
                            row[0] * bitmap.width, row[1] * bitmap.height,
                            row[2] * bitmap.width, row[3] * bitmap.height
                        )
                    )
                }
            }
        }
    }

    private fun bitmapToNormalizedFloatBuffer(bitmap: Bitmap): FloatBuffer {
        val scaled = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, true)
        val buffer = FloatBuffer.allocate(3 * INPUT_SIZE * INPUT_SIZE)
        val pixels = IntArray(INPUT_SIZE * INPUT_SIZE)
        scaled.getPixels(pixels, 0, INPUT_SIZE, 0, 0, INPUT_SIZE, INPUT_SIZE)
        for (channel in 0 until 3) {
            for (pixel in pixels) {
                val value = when (channel) {
                    0 -> (pixel shr 16 and 0xFF)
                    1 -> (pixel shr 8 and 0xFF)
                    else -> (pixel and 0xFF)
                }
                buffer.put(value / 255f)
            }
        }
        buffer.rewind()
        return buffer
    }

    override fun close() {
        session?.close()
        session = null
    }

    companion object {
        private const val INPUT_SIZE = 320
    }
}
