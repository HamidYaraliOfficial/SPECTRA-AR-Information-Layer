package com.spectra.ar.ocr

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognizer
import com.spectra.ar.vision.BoundingBox
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/** Real-time on-device OCR via ML Kit Text Recognition v2. Confidence is approximated
 *  from block/line/element structure since ML Kit doesn't expose a raw score — SPECTRA
 *  derives one from recognized-element density so the confidence filter still behaves
 *  sensibly. */
@Singleton
class OcrEngine @Inject constructor(
    private val textRecognizer: TextRecognizer,
    private val languageDetector: LanguageDetector
) {
    suspend fun recognize(bitmap: Bitmap, rotationDegrees: Int, confidenceThreshold: Float): List<TextRegion> {
        val image = InputImage.fromBitmap(bitmap, rotationDegrees)
        val result = textRecognizer.process(image).await()

        val regions = mutableListOf<TextRegion>()
        for (block in result.textBlocks) {
            val box = block.boundingBox ?: continue
            val elementCount = block.lines.sumOf { it.elements.size }
            val approxConfidence = (0.55f + (elementCount.coerceAtMost(10) * 0.04f)).coerceAtMost(0.98f)
            if (approxConfidence < confidenceThreshold) continue
            val language = languageDetector.identify(block.text)
            regions += TextRegion(
                content = block.text,
                boundingBox = BoundingBox(box.left.toFloat(), box.top.toFloat(), box.right.toFloat(), box.bottom.toFloat()),
                languageTag = language,
                confidence = approxConfidence
            )
        }
        return regions
    }
}
