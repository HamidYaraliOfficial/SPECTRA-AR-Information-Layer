package com.spectra.ar.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/** A user-placed spatial anchor in the real world — the atomic unit of the Personal AR Memory Layer. */
@Entity(tableName = "ar_markers")
data class ArMarkerEntity(
    @PrimaryKey val id: String,
    val cloudAnchorId: String?,
    val label: String,
    val latitude: Double?,
    val longitude: Double?,
    val altitude: Double?,
    /** ARCore local pose, stored so the marker can be re-attached after a session restart. */
    val poseTranslationX: Float,
    val poseTranslationY: Float,
    val poseTranslationZ: Float,
    val poseRotationQx: Float,
    val poseRotationQy: Float,
    val poseRotationQz: Float,
    val poseRotationQw: Float,
    val lensId: String,
    val tag: String?,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long
)
