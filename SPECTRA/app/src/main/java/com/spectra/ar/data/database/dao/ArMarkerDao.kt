package com.spectra.ar.data.database.dao

import androidx.room.*
import com.spectra.ar.data.database.entities.ArMarkerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArMarkerDao {
    @Query("SELECT * FROM ar_markers ORDER BY updatedAtEpochMillis DESC")
    fun observeAll(): Flow<List<ArMarkerEntity>>

    @Query("SELECT * FROM ar_markers WHERE id = :id")
    suspend fun getById(id: String): ArMarkerEntity?

    @Query("SELECT * FROM ar_markers WHERE lensId = :lensId ORDER BY updatedAtEpochMillis DESC")
    fun observeByLens(lensId: String): Flow<List<ArMarkerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(marker: ArMarkerEntity)

    @Delete
    suspend fun delete(marker: ArMarkerEntity)

    @Query("DELETE FROM ar_markers WHERE id = :id")
    suspend fun deleteById(id: String)
}
