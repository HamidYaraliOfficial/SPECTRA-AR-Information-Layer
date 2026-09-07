package com.spectra.ar.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/** A personally registered/tagged object (e.g. "my bike"), distinct from ad-hoc live detections. */
@Entity(tableName = "registered_objects")
data class ObjectEntity(
    @PrimaryKey val id: String,
    val label: String,
    val modelClassName: String?,
    val referenceImagePath: String?,
    val notes: String?,
    val createdAtEpochMillis: Long
)
