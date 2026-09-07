package com.spectra.ar.data.database.entities

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

/**
 * SQLite FTS4 shadow tables powering the local Search Engine. Each mirrors the
 * searchable text of its source table; the app keeps them in sync on every write
 * (see SearchRepositoryImpl) rather than relying on Room content-sync (kept explicit
 * for clarity across Room versions).
 */
@Fts4
@Entity(tableName = "notes_fts")
data class NoteFts(@PrimaryKey @androidx.room.ColumnInfo(name = "rowid") val rowId: Int, val docId: String, val body: String)

@Fts4
@Entity(tableName = "tasks_fts")
data class TaskFts(@PrimaryKey @androidx.room.ColumnInfo(name = "rowid") val rowId: Int, val docId: String, val title: String, val notes: String)

@Fts4
@Entity(tableName = "ocr_fts")
data class OcrFts(@PrimaryKey @androidx.room.ColumnInfo(name = "rowid") val rowId: Int, val docId: String, val content: String)

@Fts4
@Entity(tableName = "places_fts")
data class PlaceFts(@PrimaryKey @androidx.room.ColumnInfo(name = "rowid") val rowId: Int, val docId: String, val name: String, val address: String)
