package com.spectra.ar.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class DetectionKind { OBJECT, TEXT, PLACE_MATCH, PERSONAL_MARKER }

@Entity(tableName = "detection_history")
data class DetectionHistoryEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val kind: DetectionKind,
    val label: String,
    val confidence: Float,
    val latitude: Double?,
    val longitude: Double?,
    val timestampEpochMillis: Long
)
