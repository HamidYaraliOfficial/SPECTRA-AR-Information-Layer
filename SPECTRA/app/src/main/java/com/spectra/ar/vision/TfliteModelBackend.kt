package com.spectra.ar.vision

import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.io.File
import java.nio.MappedByteBuffer

/** TensorFlow Lite backend for custom, user-installed detection models. */
class TfliteModelBackend(override val modelId: String, private val labels: List<String>) : CustomModelBackend {

    private var interpreter: Interpreter? = null
    private val imageProcessor = ImageProcessor.Builder()
        .add(ResizeOp(INPUT_SIZE, INPUT_SIZE, ResizeOp.ResizeMethod.BILINEAR))
        .build()

    override fun loadFromFile(path: String) {
        val buffer: MappedByteBuffer = FileUtil.loadMappedFile(File(path).parentFile!!, File(path).name)
        val options = Interpreter.Options().apply { setNumThreads(4) }
        interpreter = Interpreter(buffer, options)
    }

    override fun infer(bitmap: Bitmap, confidenceThreshold: Float): List<DetectedObject> {
        val session = interpreter ?: return emptyList()
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))

        // Output layout assumed: [1, N, 6] => [x1, y1, x2, y2, score, classIndex], normalized 0..1.
        val output = Array(1) { Array(MAX_DETECTIONS) { FloatArray(6) } }
        session.run(tensorImage.buffer, output)

        return output[0].mapNotNull { row ->
            val score = row[4]
            if (score < confidenceThreshold) return@mapNotNull null
            val classIndex = row[5].toInt()
            val label = labels.getOrElse(classIndex) { "object" }
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

    override fun close() {
        interpreter?.close()
        interpreter = null
    }

    companion object {
        private const val INPUT_SIZE = 320
        private const val MAX_DETECTIONS = 25
    }
}
