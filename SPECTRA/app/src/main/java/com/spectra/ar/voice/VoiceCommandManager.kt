package com.spectra.ar.voice

import android.content.Context
import android.content.Intent
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

sealed class VoiceCommandEvent {
    data object Listening : VoiceCommandEvent()
    data class Result(val text: String) : VoiceCommandEvent()
    data class Error(val message: String) : VoiceCommandEvent()
}

/**
 * Hands-free trigger for the AI Vision Assistant ("SPECTRA, what's this?"). Entirely
 * optional — off by default, and only ever active while the user is holding down the
 * Ask SPECTRA button or has explicitly enabled always-listening mode in Settings.
 */
@Singleton
class VoiceCommandManager @Inject constructor() {

    fun listen(context: Context): Flow<VoiceCommandEvent> = callbackFlow {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            trySend(VoiceCommandEvent.Error("Speech recognition unavailable on this device"))
            close()
            return@callbackFlow
        }
        val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: android.os.Bundle?) { trySend(VoiceCommandEvent.Listening) }
            override fun onResults(results: android.os.Bundle) {
                val text = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                if (text != null) trySend(VoiceCommandEvent.Result(text)) else trySend(VoiceCommandEvent.Error("No speech recognized"))
                close()
            }
            override fun onError(error: Int) { trySend(VoiceCommandEvent.Error("Speech error code $error")); close() }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: android.os.Bundle?) {}
            override fun onEvent(eventType: Int, params: android.os.Bundle?) {}
        }
        recognizer.setRecognitionListener(listener)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        }
        recognizer.startListening(intent)
        awaitClose { recognizer.destroy() }
    }
}
