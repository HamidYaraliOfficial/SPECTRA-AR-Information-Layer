package com.spectra.ar.analytics

import com.spectra.ar.data.database.dao.SessionDao
import com.spectra.ar.data.database.entities.SessionEntity
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/** Tracks one AR session's lifecycle (lens used, duration, feature usage) — purely local,
 *  and only counted toward the Analytics Dashboard if the user has given consent. */
@Singleton
class SessionManager @Inject constructor(
    private val sessionDao: SessionDao
) {
    private var currentSessionId: String? = null
    private var objectDetections = 0
    private var ocrCount = 0
    private var notesCreated = 0

    suspend fun startSession(lensId: String, locationGranted: Boolean, analyticsConsent: Boolean): String {
        val id = UUID.randomUUID().toString()
        currentSessionId = id
        objectDetections = 0; ocrCount = 0; notesCreated = 0
        sessionDao.upsert(
            SessionEntity(
                id = id, lensId = lensId, startedAtEpochMillis = System.currentTimeMillis(), endedAtEpochMillis = null,
                locationPermissionGranted = locationGranted, analyticsConsentGiven = analyticsConsent
            )
        )
        return id
    }

    fun recordObjectDetection() { objectDetections++ }
    fun recordOcr() { ocrCount++ }
    fun recordNoteCreated() { notesCreated++ }

    suspend fun endSession(lensId: String, locationGranted: Boolean, analyticsConsent: Boolean) {
        val id = currentSessionId ?: return
        sessionDao.upsert(
            SessionEntity(
                id = id, lensId = lensId, startedAtEpochMillis = System.currentTimeMillis(),
                endedAtEpochMillis = System.currentTimeMillis(), locationPermissionGranted = locationGranted,
                objectDetectionCount = objectDetections, ocrCount = ocrCount, notesCreated = notesCreated,
                analyticsConsentGiven = analyticsConsent
            )
        )
        currentSessionId = null
    }
}
