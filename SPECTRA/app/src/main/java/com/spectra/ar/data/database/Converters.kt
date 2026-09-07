package com.spectra.ar.data.database

import androidx.room.TypeConverter
import com.spectra.ar.data.database.entities.DetectionKind
import com.spectra.ar.data.database.entities.OpeningHoursOwnerType
import com.spectra.ar.data.database.entities.PlaceSource
import com.spectra.ar.data.database.entities.TaskPriority
import com.spectra.ar.data.database.entities.TaskStatus

class Converters {
    @TypeConverter fun fromTaskStatus(v: TaskStatus): String = v.name
    @TypeConverter fun toTaskStatus(v: String): TaskStatus = TaskStatus.valueOf(v)

    @TypeConverter fun fromTaskPriority(v: TaskPriority): String = v.name
    @TypeConverter fun toTaskPriority(v: String): TaskPriority = TaskPriority.valueOf(v)

    @TypeConverter fun fromPlaceSource(v: PlaceSource): String = v.name
    @TypeConverter fun toPlaceSource(v: String): PlaceSource = PlaceSource.valueOf(v)

    @TypeConverter fun fromDetectionKind(v: DetectionKind): String = v.name
    @TypeConverter fun toDetectionKind(v: String): DetectionKind = DetectionKind.valueOf(v)

    @TypeConverter fun fromOwnerType(v: OpeningHoursOwnerType): String = v.name
    @TypeConverter fun toOwnerType(v: String): OpeningHoursOwnerType = OpeningHoursOwnerType.valueOf(v)
}
