package com.spectra.ar.ocr.translation

interface TranslationProvider {
    suspend fun translate(text: String, sourceLanguageTag: String?, targetLanguageTag: String): Result<String>
    suspend fun ensureModelDownloaded(languageTag: String): Result<Unit>
}
