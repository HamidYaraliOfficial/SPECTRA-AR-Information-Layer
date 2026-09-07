package com.spectra.ar.data.database.dao

import androidx.room.*
import com.spectra.ar.data.database.entities.PlaceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {
    @Query("SELECT * FROM places ORDER BY createdAtEpochMillis DESC")
    fun observeAll(): Flow<List<PlaceEntity>>

    @Query("""
        SELECT * FROM places
        WHERE latitude BETWEEN :minLat AND :maxLat
        AND longitude BETWEEN :minLng AND :maxLng
    """)
    suspend fun findWithinBoundingBox(minLat: Double, maxLat: Double, minLng: Double, maxLng: Double): List<PlaceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(place: PlaceEntity)

    @Query("DELETE FROM places WHERE id = :id")
    suspend fun deleteById(id: String)
}
