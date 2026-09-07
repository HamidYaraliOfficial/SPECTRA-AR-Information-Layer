package com.spectra.ar.spatial

import com.spectra.ar.data.database.dao.NoteDao
import com.spectra.ar.data.database.entities.NoteEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/** The Spatial Journal: notes, photos and voice memos tied to real-world anchors, and
 *  resurfaced automatically when the user is physically back near that anchor. */
@Singleton
class SpatialJournalManager @Inject constructor(
    private val noteDao: NoteDao
) {
    fun observeAll(): Flow<List<NoteEntity>> = noteDao.observeAll()
    fun observeForMarker(markerId: String): Flow<List<NoteEntity>> = noteDao.observeForMarker(markerId)

    suspend fun addNote(markerId: String?, body: String, sourceText: String? = null, sourceLanguage: String? = null, remindAt: Long? = null) {
        noteDao.upsert(
            NoteEntity(
                id = UUID.randomUUID().toString(),
                markerId = markerId,
                body = body,
                sourceText = sourceText,
                sourceLanguage = sourceLanguage,
                createdAtEpochMillis = System.currentTimeMillis(),
                remindAtEpochMillis = remindAt
            )
        )
    }

    suspend fun delete(noteId: String) = noteDao.deleteById(noteId)
    suspend fun dueReminders(now: Long = System.currentTimeMillis()) = noteDao.getDueReminders(now)
}
