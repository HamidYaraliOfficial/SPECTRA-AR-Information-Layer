package com.spectra.ar.ocr

import com.spectra.ar.vision.BoundingBox

data class TextRegion(
    val content: String,
    val boundingBox: BoundingBox,
    val languageTag: String?,
    val confidence: Float
)
