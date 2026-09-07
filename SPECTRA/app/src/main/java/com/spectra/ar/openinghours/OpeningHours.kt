package com.spectra.ar.openinghours

import kotlinx.serialization.Serializable

/**
 * Fully user-entered opening-hours model for any AR marker, note or place.
 * SPECTRA never fetches hours from an external source — the user is the only
 * data source, and every open/closed computation happens on-device.
 */
@Serializable
data class TimeRange(
    /** Minutes since local midnight, 0..1439. */
    val openMinutes: Int,
    /**
     * Minutes since local midnight. If closeMinutes <= openMinutes the range is
     * treated as crossing midnight (e.g. 22:00 -> 02:00 is stored as 1320 -> 120).
     */
    val closeMinutes: Int
) {
    val crossesMidnight: Boolean get() = closeMinutes <= openMinutes

    /** Length of this range in minutes, correctly handling the overnight case. */
    fun durationMinutes(): Int =
        if (crossesMidnight) (1440 - openMinutes) + closeMinutes else closeMinutes - openMinutes
}

@Serializable
data class DayHours(
    /** ISO-8601 day of week: 1 = Monday … 7 = Sunday. */
    val dayOfWeek: Int,
    val isClosed: Boolean = false,
    val isOpen24Hours: Boolean = false,
    val ranges: List<TimeRange> = emptyList()
)

@Serializable
data class OpeningHours(
    val days: List<DayHours> = defaultWeek(),
    /** True until the user has actually entered anything; the calculator reports UNKNOWN. */
    val isConfigured: Boolean = false
) {
    companion object {
        fun defaultWeek(): List<DayHours> = (1..7).map { DayHours(dayOfWeek = it, isClosed = true) }

        fun empty(): OpeningHours = OpeningHours(days = defaultWeek(), isConfigured = false)
    }
}
