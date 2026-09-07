package com.spectra.ar.data.database.dao

import androidx.room.*
import com.spectra.ar.data.database.entities.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM ar_sessions ORDER BY startedAtEpochMillis DESC LIMIT :limit")
    fun observeRecent(limit: Int = 100): Flow<List<SessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(session: SessionEntity)

    @Query("SELECT COUNT(*) FROM ar_sessions")
    suspend fun count(): Int

    @Query("SELECT SUM(objectDetectionCount) FROM ar_sessions")
    suspend fun totalObjectDetections(): Int?

    @Query("SELECT SUM(ocrCount) FROM ar_sessions")
    suspend fun totalOcrSessions(): Int?

    @Query("DELETE FROM ar_sessions WHERE startedAtEpochMillis < :beforeEpochMillis")
    suspend fun deleteOlderThan(beforeEpochMillis: Long)
}
