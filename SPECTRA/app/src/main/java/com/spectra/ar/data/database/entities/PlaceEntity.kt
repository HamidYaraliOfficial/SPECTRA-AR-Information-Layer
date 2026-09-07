package com.spectra.ar.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class PlaceSource { USER_ADDED, PROVIDER_LOOKUP }

@Entity(tableName = "places")
data class PlaceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String?,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val source: PlaceSource,
    /** External provider data is never persisted beyond what the user chose to save. */
    val persistedByUserConsent: Boolean,
    val createdAtEpochMillis: Long
)
