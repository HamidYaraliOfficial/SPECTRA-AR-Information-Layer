package com.spectra.ar.ai

import android.graphics.Bitmap
import com.spectra.ar.data.preferences.PrivacyPreferences
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/** Orchestrates local-first, cloud-optional answers. Cloud is only ever attempted after
 *  the caller has already obtained explicit per-request user confirmation. */
@Singleton
class AiVisionAssistant @Inject constructor(
    private val localAiProvider: LocalAiProvider,
    private val cloudAiProvider: CloudAiProvider,
    private val privacyPreferences: PrivacyPreferences
) {
    suspend fun ask(query: AiVisionQuery, frame: Bitmap?, userApprovedCloudThisRequest: Boolean): Result<AiAssistantResponse> {
        val cloudAllowed = privacyPreferences.cloudAiAllowed.first()
        if (cloudAllowed && userApprovedCloudThisRequest) {
            val cloudResult = cloudAiProvider.answer(query, frame)
            if (cloudResult.isSuccess) return cloudResult
        }
        return localAiProvider.answer(query, frame)
    }
}
