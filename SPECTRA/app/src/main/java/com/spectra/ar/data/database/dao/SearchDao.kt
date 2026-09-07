package com.spectra.ar.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.spectra.ar.data.database.entities.NoteEntity
import com.spectra.ar.data.database.entities.OcrResultEntity
import com.spectra.ar.data.database.entities.PlaceEntity
import com.spectra.ar.data.database.entities.TaskEntity

/** Backed by SQLite FTS4 virtual tables declared alongside the database (see SpectraDatabase). */
@Dao
interface SearchDao {
    @Query("SELECT ar_notes.* FROM ar_notes JOIN notes_fts ON ar_notes.id = notes_fts.docId WHERE notes_fts MATCH :query")
    suspend fun searchNotes(query: String): List<NoteEntity>

    @Query("SELECT ar_tasks.* FROM ar_tasks JOIN tasks_fts ON ar_tasks.id = tasks_fts.docId WHERE tasks_fts MATCH :query")
    suspend fun searchTasks(query: String): List<TaskEntity>

    @Query("SELECT ocr_results.* FROM ocr_results JOIN ocr_fts ON ocr_results.id = ocr_fts.docId WHERE ocr_fts MATCH :query")
    suspend fun searchOcr(query: String): List<OcrResultEntity>

    @Query("SELECT places.* FROM places JOIN places_fts ON places.id = places_fts.docId WHERE places_fts MATCH :query")
    suspend fun searchPlaces(query: String): List<PlaceEntity>
}
