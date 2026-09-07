package com.spectra.ar.workers

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.spectra.ar.core.util.SpectraConstants
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkScheduler @Inject constructor(
    private val workManager: WorkManager
) {
    fun scheduleDailyCleanup() {
        val request = PeriodicWorkRequestBuilder<DataCleanupWorker>(1, TimeUnit.DAYS)
            .setConstraints(Constraints.Builder().setRequiresBatteryNotLow(true).build())
            .addTag(SpectraConstants.WORK_TAG_CLEANUP)
            .build()
        workManager.enqueueUniquePeriodicWork(SpectraConstants.WORK_TAG_CLEANUP, ExistingPeriodicWorkPolicy.KEEP, request)
    }

    fun scheduleModelDownload(modelId: String) {
        val request = androidx.work.OneTimeWorkRequestBuilder<ModelDownloadWorker>()
            .setInputData(androidx.work.workDataOf(ModelDownloadWorker.KEY_MODEL_ID to modelId))
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.UNMETERED).build())
            .setBackoffCriteria(androidx.work.BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .addTag(SpectraConstants.WORK_TAG_MODEL_DOWNLOAD)
            .build()
        workManager.enqueueUniqueWork("model_download_$modelId", androidx.work.ExistingWorkPolicy.KEEP, request)
    }
}
