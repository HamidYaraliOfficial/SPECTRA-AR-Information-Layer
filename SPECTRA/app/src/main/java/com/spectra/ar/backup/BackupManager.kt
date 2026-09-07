package com.spectra.ar.backup

import com.spectra.ar.data.database.dao.ArMarkerDao
import com.spectra.ar.data.database.dao.NoteDao
import com.spectra.ar.data.database.dao.TaskDao
import com.spectra.ar.security.EncryptionHelper
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/** Exports Notes/Tasks/Markers as a single encrypted JSON file the user can move to
 *  another device or store themselves; restore reverses the process. Encryption uses the
 *  same Keystore-backed [EncryptionHelper] as the rest of SPECTRA's sensitive data. */
@Singleton
class BackupManager @Inject constructor(
    private val markerDao: ArMarkerDao,
    private val noteDao: NoteDao,
    private val taskDao: TaskDao,
    private val encryptionHelper: EncryptionHelper,
    private val json: Json
) {
    suspend fun exportTo(file: File): Result<Unit> = runCatching {
        val payload = BackupPayload(
            markers = markerDao.observeAll().first(),
            notes = noteDao.observeAll().first(),
            tasks = taskDao.observeAll().first(),
            exportedAtEpochMillis = System.currentTimeMillis()
        )
        val plainJson = json.encodeToString(payload)
        val encrypted = encryptionHelper.encrypt(plainJson, keyAlias = "spectra_backup_key")
        file.writeText(encrypted)
    }

    suspend fun restoreFrom(file: File): Result<BackupPayload> = runCatching {
        val encrypted = file.readText()
        val plainJson = encryptionHelper.decrypt(encrypted, keyAlias = "spectra_backup_key")
        val payload = json.decodeFromString(BackupPayload.serializer(), plainJson)
        payload.markers.forEach { markerDao.upsert(it) }
        payload.notes.forEach { noteDao.upsert(it) }
        payload.tasks.forEach { taskDao.upsert(it) }
        payload
    }
}
