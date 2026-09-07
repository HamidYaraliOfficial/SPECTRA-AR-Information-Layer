package com.spectra.ar.notifications

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpatialReminderScheduler @Inject constructor(
    private val workManager: WorkManager
) {
    fun scheduleReminder(noteId: String, title: String, body: String, triggerAtEpochMillis: Long) {
        val delay = (triggerAtEpochMillis - System.currentTimeMillis()).coerceAtLeast(0)
        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf("title" to title, "body" to body, "id" to noteId.hashCode()))
            .setConstraints(Constraints.Builder().build())
            .build()
        workManager.enqueueUniqueWork("reminder_$noteId", ExistingWorkPolicy.REPLACE, request)
    }

    fun cancel(noteId: String) = workManager.cancelUniqueWork("reminder_$noteId")
}
