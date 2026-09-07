package com.spectra.ar.workers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File
import java.io.FileOutputStream

/** Generates downsized thumbnails for the Smart Gallery so scrolling through captures
 *  stays smooth even with a large library. */
@HiltWorker
class ThumbnailWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val sourcePath = inputData.getString("source_path") ?: return Result.failure()
        val source = File(sourcePath)
        if (!source.exists()) return Result.failure()

        return try {
            val bitmap = BitmapFactory.decodeFile(sourcePath) ?: return Result.failure()
            val scale = THUMBNAIL_SIZE.toFloat() / maxOf(bitmap.width, bitmap.height)
            val thumb = Bitmap.createScaledBitmap(bitmap, (bitmap.width * scale).toInt(), (bitmap.height * scale).toInt(), true)
            val outFile = File(source.parentFile, "thumb_${source.name}")
            FileOutputStream(outFile).use { thumb.compress(Bitmap.CompressFormat.JPEG, 85, it) }
            Result.success()
        } catch (t: Throwable) {
            Result.failure()
        }
    }

    companion object { private const val THUMBNAIL_SIZE = 256 }
}
