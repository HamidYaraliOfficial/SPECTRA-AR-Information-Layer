package com.spectra.ar.privacy

import com.spectra.ar.data.database.dao.HistoryDao
import com.spectra.ar.data.database.dao.SessionDao
import com.spectra.ar.data.preferences.DataRetentionSettings
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/** Executed periodically by [com.spectra.ar.workers.DataCleanupWorker]; purges anything
 *  older than the user's chosen retention window for each data category. */
@Singleton
class DataRetentionPolicy @Inject constructor(
    private val historyDao: HistoryDao,
    private val sessionDao: SessionDao
) {
    suspend fun apply(settings: DataRetentionSettings) {
        if (!settings.autoDeleteEnabled) return
        val now = System.currentTimeMillis()
        historyDao.deleteOcrOlderThan(now - TimeUnit.DAYS.toMillis(settings.ocrDays.toLong()))
        historyDao.deleteDetectionsOlderThan(now - TimeUnit.DAYS.toMillis(settings.detectionDays.toLong()))
        sessionDao.deleteOlderThan(now - TimeUnit.DAYS.toMillis(settings.sessionDays.toLong()))
    }
}
