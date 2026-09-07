package com.spectra.ar.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.spectra.ar.data.database.dao.ArMarkerDao
import com.spectra.ar.data.database.dao.HistoryDao
import com.spectra.ar.data.database.dao.NoteDao
import com.spectra.ar.data.database.dao.OpeningHoursDao
import com.spectra.ar.data.database.dao.PlaceDao
import com.spectra.ar.data.database.dao.SearchDao
import com.spectra.ar.data.database.dao.SessionDao
import com.spectra.ar.data.database.dao.TaskDao
import com.spectra.ar.data.database.entities.ArMarkerEntity
import com.spectra.ar.data.database.entities.DetectionHistoryEntity
import com.spectra.ar.data.database.entities.NoteEntity
import com.spectra.ar.data.database.entities.NoteFts
import com.spectra.ar.data.database.entities.ObjectEntity
import com.spectra.ar.data.database.entities.OcrFts
import com.spectra.ar.data.database.entities.OcrResultEntity
import com.spectra.ar.data.database.entities.OpeningHoursEntity
import com.spectra.ar.data.database.entities.PlaceEntity
import com.spectra.ar.data.database.entities.PlaceFts
import com.spectra.ar.data.database.entities.SessionEntity
import com.spectra.ar.data.database.entities.TaskEntity
import com.spectra.ar.data.database.entities.TaskFts

@Database(
    entities = [
        ArMarkerEntity::class, NoteEntity::class, TaskEntity::class, PlaceEntity::class,
        OpeningHoursEntity::class, DetectionHistoryEntity::class, OcrResultEntity::class,
        SessionEntity::class, ObjectEntity::class,
        NoteFts::class, TaskFts::class, OcrFts::class, PlaceFts::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class SpectraDatabase : RoomDatabase() {
    abstract fun arMarkerDao(): ArMarkerDao
    abstract fun noteDao(): NoteDao
    abstract fun taskDao(): TaskDao
    abstract fun placeDao(): PlaceDao
    abstract fun openingHoursDao(): OpeningHoursDao
    abstract fun sessionDao(): SessionDao
    abstract fun historyDao(): HistoryDao
    abstract fun searchDao(): SearchDao
}
