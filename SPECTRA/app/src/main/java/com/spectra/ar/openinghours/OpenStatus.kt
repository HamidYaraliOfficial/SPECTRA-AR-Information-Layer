package com.spectra.ar.openinghours

/** Live status of a place/marker, entirely derived from user-entered [OpeningHours]. */
enum class OpenStatus { OPEN, CLOSED, CLOSING_SOON, OPENING_SOON, UNKNOWN }

data class OpeningHoursSnapshot(
    val status: OpenStatus,
    /** Wall-clock instant (epoch millis) of the next open/close transition, or null if none exists (e.g. 24h open with no closures ever). */
    val nextChangeAtEpochMillis: Long?,
    /** Minutes remaining until [nextChangeAtEpochMillis], for display/countdown purposes. */
    val minutesUntilChange: Long?
)
