package com.spectra.ar.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class OpeningHoursOwnerType { PLACE, MARKER }

/**
 * Stores the OpeningHours the user typed in for a place or a personal marker, as JSON.
 * SPECTRA never populates this table from an external source.
 */
@Entity(tableName = "opening_hours")
data class OpeningHoursEntity(
    @PrimaryKey val ownerId: String,
    val ownerType: OpeningHoursOwnerType,
    val openingHoursJson: String,
    val closingSoonThresholdMinutes: Int = 30,
    val updatedAtEpochMillis: Long
)
