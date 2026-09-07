package com.spectra.ar.data.database.dao

import androidx.room.*
import com.spectra.ar.data.database.entities.DetectionHistoryEntity
import com.spectra.ar.data.database.entities.OcrResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM detection_history ORDER BY timestampEpochMillis DESC LIMIT :limit")
    fun observeRecentDetections(limit: Int = 200): Flow<List<DetectionHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetection(entity: DetectionHistoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOcrResult(entity: OcrResultEntity)

    @Query("SELECT * FROM ocr_results ORDER BY timestampEpochMillis DESC LIMIT :limit")
    fun observeRecentOcr(limit: Int = 200): Flow<List<OcrResultEntity>>

    @Query("DELETE FROM detection_history")
    suspend fun clearDetections()

    @Query("DELETE FROM ocr_results")
    suspend fun clearOcrResults()

    @Query("DELETE FROM detection_history WHERE timestampEpochMillis < :beforeEpochMillis")
    suspend fun deleteDetectionsOlderThan(beforeEpochMillis: Long)

    @Query("DELETE FROM ocr_results WHERE timestampEpochMillis < :beforeEpochMillis")
    suspend fun deleteOcrOlderThan(beforeEpochMillis: Long)
}
