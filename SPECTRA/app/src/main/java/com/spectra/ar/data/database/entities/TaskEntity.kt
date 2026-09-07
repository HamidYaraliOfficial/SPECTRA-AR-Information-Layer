package com.spectra.ar.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TaskPriority { LOW, NORMAL, HIGH }
enum class TaskStatus { OPEN, DONE, CANCELLED }

@Entity(tableName = "ar_tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val markerId: String?,
    val title: String,
    val notes: String?,
    val status: TaskStatus,
    val priority: TaskPriority,
    val dueAtEpochMillis: Long?,
    val reminderAtEpochMillis: Long?,
    val createdAtEpochMillis: Long
)
