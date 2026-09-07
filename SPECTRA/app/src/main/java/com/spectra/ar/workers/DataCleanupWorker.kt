package com.spectra.ar.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.spectra.ar.data.preferences.PrivacyPreferences
import com.spectra.ar.privacy.DataRetentionPolicy
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class DataCleanupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val retentionPolicy: DataRetentionPolicy,
    private val privacyPreferences: PrivacyPreferences
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = try {
        retentionPolicy.apply(privacyPreferences.retentionSettings.first())
        Result.success()
    } catch (t: Throwable) {
        Result.retry()
    }
}
