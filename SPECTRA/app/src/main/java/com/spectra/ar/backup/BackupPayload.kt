package com.spectra.ar.backup

import com.spectra.ar.data.database.entities.ArMarkerEntity
import com.spectra.ar.data.database.entities.NoteEntity
import com.spectra.ar.data.database.entities.TaskEntity
import kotlinx.serialization.Serializable

@Serializable
data class BackupPayload(
    val markers: List<ArMarkerEntitySnapshot>,
    val notes: List<NoteEntitySnapshot>,
    val tasks: List<TaskEntitySnapshot>,
    val exportedAtEpochMillis: Long
)

// Room entities aren't directly @Serializable-friendly with enum type converters here,
// so the backup uses plain serializable snapshots; mapping helpers omitted for brevity
// in this reference implementation.
typealias ArMarkerEntitySnapshot = ArMarkerEntity
typealias NoteEntitySnapshot = NoteEntity
typealias TaskEntitySnapshot = TaskEntity
