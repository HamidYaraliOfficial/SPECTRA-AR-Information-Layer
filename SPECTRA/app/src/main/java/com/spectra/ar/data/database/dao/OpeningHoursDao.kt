package com.spectra.ar.data.database.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Dao
import androidx.room.Query
import com.spectra.ar.data.database.entities.OpeningHoursEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OpeningHoursDao {
    @Query("SELECT * FROM opening_hours WHERE ownerId = :ownerId")
    fun observe(ownerId: String): Flow<OpeningHoursEntity?>

    @Query("SELECT * FROM opening_hours WHERE ownerId = :ownerId")
    suspend fun get(ownerId: String): OpeningHoursEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: OpeningHoursEntity)

    @Query("DELETE FROM opening_hours WHERE ownerId = :ownerId")
    suspend fun deleteFor(ownerId: String)
}
