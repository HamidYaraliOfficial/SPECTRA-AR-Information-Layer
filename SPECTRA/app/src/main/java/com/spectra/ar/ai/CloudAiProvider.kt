package com.spectra.ar.ai

import android.graphics.Bitmap
import com.spectra.ar.data.preferences.PrivacyPreferences
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Optional Cloud AI provider. Every call is gated on the user's live
 * [PrivacyPreferences.cloudAiAllowed] flag — this class refuses to send anything if that
 * flag is false, regardless of caller. The HUD additionally asks for a per-request
 * "Allow once / Don't send" confirmation before this is ever invoked (see AiActionSystem).
 */
@Singleton
class CloudAiProvider @Inject constructor(
    private val privacyPreferences: PrivacyPreferences
) : AiProvider {
    override val requiresNetwork = true

    override suspend fun answer(query: AiVisionQuery, frame: Bitmap?): Result<AiAssistantResponse> {
        val allowed = privacyPreferences.cloudAiAllowed.first()
        if (!allowed) return Result.failure(SecurityException("Cloud AI is disabled in Privacy settings"))

        return runCatching {
            // Real implementation would POST PromptBuilder.build(query) + an optionally
            // downscaled `frame` to the configured Cloud AI endpoint via Retrofit
            // (see core/di/NetworkModule) and parse structured actions from the response.
            throw NotImplementedError("Wire up to your chosen Cloud AI endpoint")
        }
    }
}
