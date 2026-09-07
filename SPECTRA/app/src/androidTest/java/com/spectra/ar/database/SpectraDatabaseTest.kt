package com.spectra.ar.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.spectra.ar.data.database.SpectraDatabase
import com.spectra.ar.data.database.entities.ArMarkerEntity
import com.spectra.ar.data.database.entities.TaskEntity
import com.spectra.ar.data.database.entities.TaskPriority
import com.spectra.ar.data.database.entities.TaskStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** Uses a plain (unencrypted) in-memory Room instance — SQLCipher's SupportFactory is
 *  swapped out here since these tests exercise schema/query correctness, not encryption
 *  (encryption itself is covered by security/EncryptionHelperTest). */
@RunWith(AndroidJUnit4::class)
class SpectraDatabaseTest {

    private lateinit var database: SpectraDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), SpectraDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() { database.close() }

    @Test
    fun insertAndReadMarker() = runTest {
        val marker = ArMarkerEntity(
            id = "marker-1", cloudAnchorId = null, label = "Desk plant", latitude = null, longitude = null, altitude = null,
            poseTranslationX = 0f, poseTranslationY = 0f, poseTranslationZ = 0f,
            poseRotationQx = 0f, poseRotationQy = 0f, poseRotationQz = 0f, poseRotationQw = 1f,
            lensId = "MEMORY", tag = "office", createdAtEpochMillis = 1L, updatedAtEpochMillis = 1L
        )
        database.arMarkerDao().upsert(marker)

        val loaded = database.arMarkerDao().getById("marker-1")
        assertThat(loaded).isEqualTo(marker)
    }

    @Test
    fun taskStatusUpdateIsPersisted() = runTest {
        val task = TaskEntity(
            id = "task-1", markerId = null, title = "Water the plant", notes = null,
            status = TaskStatus.OPEN, priority = TaskPriority.LOW, dueAtEpochMillis = null,
            reminderAtEpochMillis = null, createdAtEpochMillis = 1L
        )
        database.taskDao().upsert(task)
        database.taskDao().updateStatus("task-1", TaskStatus.DONE)

        val open = database.taskDao().observeByStatus(TaskStatus.OPEN).first()
        val done = database.taskDao().observeByStatus(TaskStatus.DONE).first()
        assertThat(open).isEmpty()
        assertThat(done.map { it.id }).containsExactly("task-1")
    }

    @Test
    fun deletingMarkerCascadesToItsNotes() = runTest {
        val marker = ArMarkerEntity(
            id = "marker-2", cloudAnchorId = null, label = "Whiteboard", latitude = null, longitude = null, altitude = null,
            poseTranslationX = 0f, poseTranslationY = 0f, poseTranslationZ = 0f,
            poseRotationQx = 0f, poseRotationQy = 0f, poseRotationQz = 0f, poseRotationQw = 1f,
            lensId = "STUDY", tag = null, createdAtEpochMillis = 1L, updatedAtEpochMillis = 1L
        )
        database.arMarkerDao().upsert(marker)
        database.noteDao().upsert(
            com.spectra.ar.data.database.entities.NoteEntity(
                id = "note-1", markerId = "marker-2", body = "Review before exam", sourceText = null,
                sourceLanguage = null, createdAtEpochMillis = 1L, remindAtEpochMillis = null
            )
        )

        database.arMarkerDao().deleteById("marker-2")

        val remainingNotes = database.noteDao().observeForMarker("marker-2").first()
        assertThat(remainingNotes).isEmpty()
    }
}
