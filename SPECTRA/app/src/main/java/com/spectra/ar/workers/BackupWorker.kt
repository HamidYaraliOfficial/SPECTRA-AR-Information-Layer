package com.spectra.ar.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.spectra.ar.backup.BackupManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File

@HiltWorker
class BackupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val backupManager: BackupManager
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val destPath = inputData.getString("dest_path") ?: return Result.failure()
        val result = backupManager.exportTo(File(destPath))
        return if (result.isSuccess) Result.success() else Result.retry()
    }
}
