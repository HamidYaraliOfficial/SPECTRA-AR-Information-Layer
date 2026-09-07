package com.spectra.ar.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.spectra.ar.models.ModelInstallState
import com.spectra.ar.models.ModelManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink

@HiltWorker
class ModelDownloadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val modelManager: ModelManager,
    private val okHttpClient: OkHttpClient
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val modelId = inputData.getString(KEY_MODEL_ID) ?: return Result.failure()
        val model = modelManager.catalog.value.firstOrNull { it.id == modelId } ?: return Result.failure()

        modelManager.markState(modelId, ModelInstallState.DOWNLOADING)
        return try {
            val request = Request.Builder().url(model.downloadUrl).build()
            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) return Result.retry()

            val targetFile = modelManager.localFileFor(model)
            targetFile.parentFile?.mkdirs()
            response.body?.source()?.use { source ->
                targetFile.sink().buffer().use { sink -> sink.writeAll(source) }
            }

            modelManager.markState(modelId, ModelInstallState.VERIFYING)
            val activated = modelManager.verifyAndActivate(model)
            if (activated) Result.success() else Result.failure()
        } catch (t: Throwable) {
            modelManager.markState(modelId, ModelInstallState.FAILED)
            Result.retry()
        }
    }

    companion object {
        const val KEY_MODEL_ID = "model_id"
    }
}
