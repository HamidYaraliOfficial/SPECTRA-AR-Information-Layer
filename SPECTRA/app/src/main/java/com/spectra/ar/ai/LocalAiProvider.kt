package com.spectra.ar.ai

import android.graphics.Bitmap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fully on-device provider: answers from what the vision/OCR pipeline already extracted,
 * with simple rule-based action suggestions. No network, no image ever leaves the device.
 * This is the default and required-minimum provider — SPECTRA works completely offline
 * through this path.
 */
@Singleton
class LocalAiProvider @Inject constructor() : AiProvider {
    override val requiresNetwork = false

    override suspend fun answer(query: AiVisionQuery, frame: Bitmap?): Result<AiAssistantResponse> = runCatching {
        val actions = mutableListOf<AiSuggestedAction>()
        val answer = when {
            !query.ocrText.isNullOrBlank() -> {
                actions += AiSuggestedAction(AiActionType.CREATE_NOTE, "Save this text as a note", mapOf("body" to query.ocrText))
                actions += AiSuggestedAction(AiActionType.TRANSLATE, "Translate this text")
                "Here's the text SPECTRA can read in this frame:\n\n${query.ocrText.take(600)}"
            }
            query.detectedObjectLabels.isNotEmpty() -> {
                actions += AiSuggestedAction(AiActionType.CREATE_TASK, "Create a task about ${query.detectedObjectLabels.first()}")
                "SPECTRA can see: ${query.detectedObjectLabels.joinToString(", ")}. Ask a more specific question, or enable Cloud AI in Settings for richer answers."
            }
            else -> "Point the camera at some text or an object and ask again — on-device answers are grounded in what SPECTRA can actually detect."
        }
        AiAssistantResponse(answerText = answer, suggestedActions = actions, usedCloud = false)
    }
}
