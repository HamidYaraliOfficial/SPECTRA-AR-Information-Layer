package com.spectra.ar.tasks

import com.spectra.ar.data.database.dao.TaskDao
import com.spectra.ar.data.database.entities.TaskEntity
import com.spectra.ar.data.database.entities.TaskPriority
import com.spectra.ar.data.database.entities.TaskStatus
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArTaskManager @Inject constructor(
    private val taskDao: TaskDao
) {
    fun observeAll(): Flow<List<TaskEntity>> = taskDao.observeAll()
    fun observeOpen(): Flow<List<TaskEntity>> = taskDao.observeByStatus(TaskStatus.OPEN)
    fun observeForMarker(markerId: String): Flow<List<TaskEntity>> = taskDao.observeForMarker(markerId)

    suspend fun createTask(
        title: String,
        markerId: String? = null,
        notes: String? = null,
        priority: TaskPriority = TaskPriority.NORMAL,
        dueAtEpochMillis: Long? = null,
        reminderAtEpochMillis: Long? = null
    ) {
        taskDao.upsert(
            TaskEntity(
                id = UUID.randomUUID().toString(), markerId = markerId, title = title, notes = notes,
                status = TaskStatus.OPEN, priority = priority, dueAtEpochMillis = dueAtEpochMillis,
                reminderAtEpochMillis = reminderAtEpochMillis, createdAtEpochMillis = System.currentTimeMillis()
            )
        )
    }

    suspend fun markDone(id: String) = taskDao.updateStatus(id, TaskStatus.DONE)
    suspend fun markCancelled(id: String) = taskDao.updateStatus(id, TaskStatus.CANCELLED)
    suspend fun delete(id: String) = taskDao.deleteById(id)
}
