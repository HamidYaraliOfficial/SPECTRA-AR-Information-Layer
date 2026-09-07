package com.spectra.ar.ai

/** Builds a compact, structured prompt from on-device context (detections, OCR, location)
 *  so both the local and cloud providers get the same grounded input. */
object PromptBuilder {
    fun build(query: AiVisionQuery): String = buildString {
        appendLine("User question: ${query.prompt}")
        if (query.detectedObjectLabels.isNotEmpty()) {
            appendLine("Objects currently visible: ${query.detectedObjectLabels.joinToString(", ")}")
        }
        if (!query.ocrText.isNullOrBlank()) {
            appendLine("Text visible in frame: ${query.ocrText.take(500)}")
        }
        query.locationContext?.let { appendLine("Location context: $it") }
        appendLine("Respond concisely. If a follow-up action makes sense (note, task, translation, reminder, bookmark, saved place, search), suggest it explicitly.")
    }
}
