package com.spectra.ar.models

enum class ModelBackendType { TFLITE, ONNX }
enum class ModelInstallState { NOT_INSTALLED, DOWNLOADING, VERIFYING, INSTALLED, FAILED }

data class ModelInfo(
    val id: String,
    val displayName: String,
    val backend: ModelBackendType,
    val sizeBytes: Long,
    val version: String,
    val downloadUrl: String,
    val sha256: String,
    val labels: List<String>,
    val installState: ModelInstallState = ModelInstallState.NOT_INSTALLED,
    val localPath: String? = null
)
