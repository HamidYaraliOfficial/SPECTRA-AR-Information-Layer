package com.spectra.ar.data.database.dao

import androidx.room.*
import com.spectra.ar.data.database.entities.TaskEntity
import com.spectra.ar.data.database.entities.TaskStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM ar_tasks ORDER BY dueAtEpochMillis IS NULL, dueAtEpochMillis ASC")
    fun observeAll(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM ar_tasks WHERE status = :status ORDER BY dueAtEpochMillis IS NULL, dueAtEpochMillis ASC")
    fun observeByStatus(status: TaskStatus): Flow<List<TaskEntity>>

    @Query("SELECT * FROM ar_tasks WHERE markerId = :markerId")
    fun observeForMarker(markerId: String): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(task: TaskEntity)

    @Query("UPDATE ar_tasks SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: TaskStatus)

    @Query("DELETE FROM ar_tasks WHERE id = :id")
    suspend fun deleteById(id: String)
}
