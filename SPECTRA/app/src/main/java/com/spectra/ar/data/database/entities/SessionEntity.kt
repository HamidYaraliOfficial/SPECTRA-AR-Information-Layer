package com.spectra.ar.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ar_sessions")
data class SessionEntity(
    @PrimaryKey val id: String,
    val lensId: String,
    val startedAtEpochMillis: Long,
    val endedAtEpochMillis: Long?,
    val locationPermissionGranted: Boolean,
    val objectDetectionCount: Int = 0,
    val ocrCount: Int = 0,
    val notesCreated: Int = 0,
    val analyticsConsentGiven: Boolean = false
)
