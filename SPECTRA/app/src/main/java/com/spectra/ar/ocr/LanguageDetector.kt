package com.spectra.ar.ocr

import com.google.mlkit.nl.languageid.LanguageIdentifier
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageDetector @Inject constructor(
    private val identifier: LanguageIdentifier
) {
    /** Returns a BCP-47 language tag, or null if confidence is too low to be useful. */
    suspend fun identify(text: String): String? {
        if (text.isBlank()) return null
        val tag = identifier.identifyLanguage(text).await()
        return if (tag == "und") null else tag
    }
}
