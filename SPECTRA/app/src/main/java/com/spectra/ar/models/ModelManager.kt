package com.spectra.ar.models

import android.content.Context
import com.spectra.ar.vision.CustomModelBackend
import com.spectra.ar.vision.OnnxModelBackend
import com.spectra.ar.vision.TfliteModelBackend
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Central registry of installable custom vision models. Downloading is delegated to
 * [com.spectra.ar.workers.ModelDownloadWorker] (WorkManager, retryable); this class tracks
 * install state, exposes the active backend to the vision pipeline, and lets the user
 * add/update/remove models from Settings > Models.
 */
@Singleton
class ModelManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val integrityChecker: ModelIntegrityChecker
) {
    private val modelsDir = File(context.filesDir, "models").apply { mkdirs() }

    private val _catalog = MutableStateFlow<List<ModelInfo>>(emptyList())
    val catalog: StateFlow<List<ModelInfo>> = _catalog.asStateFlow()

    private var activeBackend: CustomModelBackend? = null

    fun setCatalog(models: List<ModelInfo>) {
        _catalog.value = models
    }

    fun localFileFor(model: ModelInfo): File = File(modelsDir, "${model.id}_${model.version}.model")

    fun markInstalled(modelId: String, localPath: String) {
        _catalog.value = _catalog.value.map {
            if (it.id == modelId) it.copy(installState = ModelInstallState.INSTALLED, localPath = localPath) else it
        }
    }

    fun markState(modelId: String, state: ModelInstallState) {
        _catalog.value = _catalog.value.map { if (it.id == modelId) it.copy(installState = state) else it }
    }

    fun verifyAndActivate(model: ModelInfo): Boolean {
        val file = localFileFor(model)
        if (!file.exists() || !integrityChecker.verify(file, model.sha256)) {
            markState(model.id, ModelInstallState.FAILED)
            return false
        }
        activeBackend?.close()
        activeBackend = when (model.backend) {
            ModelBackendType.TFLITE -> TfliteModelBackend(model.id, model.labels)
            ModelBackendType.ONNX -> OnnxModelBackend(model.id, model.labels)
        }.also { it.loadFromFile(file.absolutePath) }
        markInstalled(model.id, file.absolutePath)
        return true
    }

    fun removeModel(model: ModelInfo) {
        localFileFor(model).delete()
        if (activeBackend?.modelId == model.id) {
            activeBackend?.close()
            activeBackend = null
        }
        _catalog.value = _catalog.value.map { if (it.id == model.id) it.copy(installState = ModelInstallState.NOT_INSTALLED, localPath = null) else it }
    }

    fun activeCustomBackend(): CustomModelBackend? = activeBackend
}
