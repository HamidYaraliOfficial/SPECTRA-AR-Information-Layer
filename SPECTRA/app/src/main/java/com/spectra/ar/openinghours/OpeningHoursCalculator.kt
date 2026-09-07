package com.spectra.ar.openinghours

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Pure, offline computation of open/closed status and time-to-next-change from
 * hours the user typed in themselves. No network call, no external "business
 * hours" API — this is the entire source of truth.
 */
@Singleton
class OpeningHoursCalculator @Inject constructor() {

    private data class AbsoluteInterval(val start: LocalDateTime, val end: LocalDateTime)

    /**
     * @param closingSoonThresholdMinutes window (in minutes) inside which OPEN becomes
     *        CLOSING_SOON and CLOSED becomes OPENING_SOON, so the AR overlay can warn the user.
     */
    fun computeSnapshot(
        hours: OpeningHours,
        now: LocalDateTime = LocalDateTime.now(),
        closingSoonThresholdMinutes: Int = 30
    ): OpeningHoursSnapshot {
        if (!hours.isConfigured) {
            return OpeningHoursSnapshot(OpenStatus.UNKNOWN, null, null)
        }

        val intervals = expandToAbsoluteIntervals(hours, now)

        if (intervals.isEmpty()) {
            // Configured, but every day is explicitly marked closed.
            return OpeningHoursSnapshot(OpenStatus.CLOSED, null, null)
        }

        val currentInterval = intervals.firstOrNull { !now.isBefore(it.start) && now.isBefore(it.end) }

        return if (currentInterval != null) {
            val minutesToClose = ChronoUnit.MINUTES.between(now, currentInterval.end)
            val status = if (minutesToClose in 0..closingSoonThresholdMinutes) OpenStatus.CLOSING_SOON else OpenStatus.OPEN
            OpeningHoursSnapshot(status, toEpochMillis(currentInterval.end), minutesToClose)
        } else {
            val nextInterval = intervals.filter { it.start.isAfter(now) }.minByOrNull { it.start }
            if (nextInterval == null) {
                OpeningHoursSnapshot(OpenStatus.CLOSED, null, null)
            } else {
                val minutesToOpen = ChronoUnit.MINUTES.between(now, nextInterval.start)
                val status = if (minutesToOpen in 0..closingSoonThresholdMinutes) OpenStatus.OPENING_SOON else OpenStatus.CLOSED
                OpeningHoursSnapshot(status, toEpochMillis(nextInterval.start), minutesToOpen)
            }
        }
    }

    fun isOpenNow(hours: OpeningHours, now: LocalDateTime = LocalDateTime.now()): Boolean {
        val status = computeSnapshot(hours, now).status
        return status == OpenStatus.OPEN || status == OpenStatus.CLOSING_SOON
    }

    /** Splits a raw minute count into whole days/hours/minutes for localized display. */
    fun toRemainingDuration(totalMinutes: Long): RemainingDuration {
        val safeMinutes = totalMinutes.coerceAtLeast(0)
        val days = safeMinutes / (24 * 60)
        val hours = (safeMinutes % (24 * 60)) / 60
        val minutes = safeMinutes % 60
        return RemainingDuration(days.toInt(), hours.toInt(), minutes.toInt())
    }

    /** Expands the user's weekly template into concrete date/time intervals covering
     *  a window from yesterday to 8 days ahead, so overnight ranges and "next week" lookups
     *  both resolve correctly regardless of where in the week [now] falls. */
    private fun expandToAbsoluteIntervals(hours: OpeningHours, now: LocalDateTime): List<AbsoluteInterval> {
        val byDayOfWeek = hours.days.associateBy { it.dayOfWeek }
        val intervals = mutableListOf<AbsoluteInterval>()

        for (dayOffset in -1..8) {
            val date = now.toLocalDate().plusDays(dayOffset.toLong())
            val isoDayOfWeek = date.dayOfWeek.value // 1 = Monday .. 7 = Sunday
            val dayHours = byDayOfWeek[isoDayOfWeek] ?: continue
            if (dayHours.isClosed && !dayHours.isOpen24Hours) continue

            if (dayHours.isOpen24Hours) {
                intervals += AbsoluteInterval(date.atStartOfDay(), date.plusDays(1).atStartOfDay())
                continue
            }

            for (range in dayHours.ranges) {
                val start = date.atStartOfDay().plusMinutes(range.openMinutes.toLong())
                val end = if (range.crossesMidnight) {
                    date.plusDays(1).atStartOfDay().plusMinutes(range.closeMinutes.toLong())
                } else {
                    date.atStartOfDay().plusMinutes(range.closeMinutes.toLong())
                }
                if (end.isAfter(start)) intervals += AbsoluteInterval(start, end)
            }
        }
        return intervals.sortedBy { it.start }
    }

    private fun toEpochMillis(dateTime: LocalDateTime): Long =
        dateTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
}

data class RemainingDuration(val days: Int, val hours: Int, val minutes: Int)
