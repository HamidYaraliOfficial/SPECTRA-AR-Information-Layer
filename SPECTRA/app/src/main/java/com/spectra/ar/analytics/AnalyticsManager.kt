package com.spectra.ar.analytics

import com.spectra.ar.data.database.dao.SessionDao
import javax.inject.Inject
import javax.inject.Singleton

data class AnalyticsSummary(
    val totalSessions: Int,
    val totalObjectDetections: Int,
    val totalOcrSessions: Int
)

/** Powers the local AR Analytics Dashboard. Reads only from the on-device session log —
 *  nothing here is ever transmitted off-device. */
@Singleton
class AnalyticsManager @Inject constructor(
    private val sessionDao: SessionDao
) {
    suspend fun summary(): AnalyticsSummary = AnalyticsSummary(
        totalSessions = sessionDao.count(),
        totalObjectDetections = sessionDao.totalObjectDetections() ?: 0,
        totalOcrSessions = sessionDao.totalOcrSessions() ?: 0
    )
}
