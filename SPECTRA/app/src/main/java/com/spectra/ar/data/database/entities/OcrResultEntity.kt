package com.spectra.ar.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ocr_results")
data class OcrResultEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val content: String,
    val languageTag: String?,
    val confidence: Float,
    val timestampEpochMillis: Long
)
