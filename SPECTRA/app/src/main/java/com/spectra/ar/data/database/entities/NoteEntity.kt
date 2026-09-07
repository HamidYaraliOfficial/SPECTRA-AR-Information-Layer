package com.spectra.ar.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ar_notes",
    foreignKeys = [
        ForeignKey(
            entity = ArMarkerEntity::class,
            parentColumns = ["id"],
            childColumns = ["markerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("markerId")]
)
data class NoteEntity(
    @PrimaryKey val id: String,
    val markerId: String?,
    val body: String,
    val sourceText: String?,
    val sourceLanguage: String?,
    val createdAtEpochMillis: Long,
    val remindAtEpochMillis: Long?
)
