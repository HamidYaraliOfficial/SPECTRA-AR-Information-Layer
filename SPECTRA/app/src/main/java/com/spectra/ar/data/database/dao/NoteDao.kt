package com.spectra.ar.data.database.dao

import androidx.room.*
import com.spectra.ar.data.database.entities.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM ar_notes ORDER BY createdAtEpochMillis DESC")
    fun observeAll(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM ar_notes WHERE markerId = :markerId ORDER BY createdAtEpochMillis DESC")
    fun observeForMarker(markerId: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM ar_notes WHERE remindAtEpochMillis IS NOT NULL AND remindAtEpochMillis <= :nowEpochMillis")
    suspend fun getDueReminders(nowEpochMillis: Long): List<NoteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(note: NoteEntity)

    @Query("DELETE FROM ar_notes WHERE id = :id")
    suspend fun deleteById(id: String)
}
