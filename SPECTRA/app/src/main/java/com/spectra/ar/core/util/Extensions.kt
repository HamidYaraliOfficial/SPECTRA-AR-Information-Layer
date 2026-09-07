package com.spectra.ar.core.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/** Wraps any Flow<T> into a Flow<SpectraResult<T>>, converting upstream exceptions to Errors. */
fun <T> Flow<T>.asResultFlow(): Flow<SpectraResult<T>> =
    map<T, SpectraResult<T>> { SpectraResult.Success(it) }
        .catch { emit(SpectraResult.Error(it, it.message)) }

fun Float.coercedConfidence(): Float = this.coerceIn(0f, 1f)
