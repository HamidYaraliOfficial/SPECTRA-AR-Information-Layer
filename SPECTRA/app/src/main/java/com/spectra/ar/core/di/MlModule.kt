package com.spectra.ar.core.di

import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MlModule {

    @Provides
    @Singleton
    fun provideTextRecognizer() = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    @Provides
    @Singleton
    fun provideLanguageIdentifier() = LanguageIdentification.getClient()
}
