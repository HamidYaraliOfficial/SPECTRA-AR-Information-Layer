package com.spectra.ar.openinghours

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Drives live "closes in 12m" / "opens in 3h" countdowns in the UI by recomputing
 * the snapshot once a minute, without any polling of external services.
 */
@Singleton
class OpeningHoursTicker @Inject constructor(
    private val calculator: OpeningHoursCalculator
) {
    fun observe(hours: OpeningHours, tickIntervalMillis: Long = 30_000L): Flow<OpeningHoursSnapshot> = flow {
        while (true) {
            emit(calculator.computeSnapshot(hours, LocalDateTime.now()))
            delay(tickIntervalMillis)
        }
    }
}
