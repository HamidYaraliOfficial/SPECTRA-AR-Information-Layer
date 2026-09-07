package com.spectra.ar.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.spectra.ar.data.database.dao.HistoryDao
import com.spectra.ar.data.database.entities.OcrResultEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/** Batches OCR results captured during a session (e.g. from Scene Recorder frames) into
 *  the local, FTS-indexed history so Search stays responsive without blocking the UI. */
@HiltWorker
class OcrBatchWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val historyDao: HistoryDao
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val sessionId = inputData.getString("session_id") ?: return Result.failure()
        val contents = inputData.getStringArray("contents") ?: return Result.success()
        contents.forEachIndexed { index, content ->
            historyDao.insertOcrResult(
                OcrResultEntity(
                    id = "$sessionId-ocr-$index", sessionId = sessionId, content = content,
                    languageTag = null, confidence = 1f, timestampEpochMillis = System.currentTimeMillis()
                )
            )
        }
        return Result.success()
    }
}
