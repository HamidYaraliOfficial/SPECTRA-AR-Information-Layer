package com.spectra.ar.core.util

/** Lightweight sealed result wrapper used across repositories and use-cases. */
sealed class SpectraResult<out T> {
    data class Success<T>(val data: T) : SpectraResult<T>()
    data class Error(val throwable: Throwable, val message: String? = null) : SpectraResult<Nothing>()
    data object Loading : SpectraResult<Nothing>()

    inline fun onSuccess(action: (T) -> Unit): SpectraResult<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (Throwable) -> Unit): SpectraResult<T> {
        if (this is Error) action(throwable)
        return this
    }
}

inline fun <T, R> SpectraResult<T>.map(transform: (T) -> R): SpectraResult<R> = when (this) {
    is SpectraResult.Success -> SpectraResult.Success(transform(data))
    is SpectraResult.Error -> this
    is SpectraResult.Loading -> SpectraResult.Loading
}
