package com.spectra.ar.ocr.translation

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/** Default, fully on-device translation provider (ML Kit Translate). A Cloud provider can
 *  be swapped in via the same [TranslationProvider] interface if the user opts in, but
 *  nothing in SPECTRA requires it. */
@Singleton
class MlKitTranslationProvider @Inject constructor() : TranslationProvider {

    override suspend fun translate(text: String, sourceLanguageTag: String?, targetLanguageTag: String): Result<String> = runCatching {
        val sourceCode = sourceLanguageTag?.let { TranslateLanguage.fromLanguageTag(it) } ?: TranslateLanguage.ENGLISH
        val targetCode = TranslateLanguage.fromLanguageTag(targetLanguageTag) ?: TranslateLanguage.ENGLISH
        val options = TranslatorOptions.Builder().setSourceLanguage(sourceCode).setTargetLanguage(targetCode).build()
        val translator = Translation.getClient(options)
        translator.downloadModelIfNeeded(DownloadConditions.Builder().requireWifi().build()).await()
        translator.translate(text).await()
    }

    override suspend fun ensureModelDownloaded(languageTag: String): Result<Unit> = runCatching {
        val code = TranslateLanguage.fromLanguageTag(languageTag) ?: return@runCatching
        val options = TranslatorOptions.Builder().setSourceLanguage(code).setTargetLanguage(TranslateLanguage.ENGLISH).build()
        Translation.getClient(options).downloadModelIfNeeded(DownloadConditions.Builder().build()).await()
    }
}
