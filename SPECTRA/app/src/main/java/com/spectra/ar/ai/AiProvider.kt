package com.spectra.ar.ai

import android.graphics.Bitmap

interface AiProvider {
    val requiresNetwork: Boolean
    suspend fun answer(query: AiVisionQuery, frame: Bitmap?): Result<AiAssistantResponse>
}
