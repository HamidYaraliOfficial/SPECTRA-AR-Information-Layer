package com.spectra.ar.ai

enum class AiActionType { CREATE_NOTE, CREATE_TASK, TRANSLATE, SEARCH, ADD_BOOKMARK, ADD_PLACE, CREATE_REMINDER }

data class AiSuggestedAction(
    val type: AiActionType,
    val summary: String,
    val payload: Map<String, String> = emptyMap(),
    /** Every action with a side effect requires explicit confirmation before it runs. */
    val requiresConfirmation: Boolean = true
)

data class AiAssistantResponse(
    val answerText: String,
    val suggestedActions: List<AiSuggestedAction> = emptyList(),
    val usedCloud: Boolean = false
)

data class AiVisionQuery(
    val prompt: String,
    val detectedObjectLabels: List<String> = emptyList(),
    val ocrText: String? = null,
    val locationContext: String? = null
)
