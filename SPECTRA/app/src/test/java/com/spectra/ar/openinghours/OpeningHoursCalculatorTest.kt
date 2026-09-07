package com.spectra.ar.openinghours

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDateTime

class OpeningHoursCalculatorTest {

    private val calculator = OpeningHoursCalculator()

    private fun dateTimeFor(dayOfWeek: Int, hour: Int, minute: Int): LocalDateTime {
        // Anchor to a known Monday (2026-08-17 is a Monday) then walk to the requested ISO day.
        val monday = LocalDateTime.of(2026, 8, 17, 0, 0)
        return monday.plusDays((dayOfWeek - 1).toLong()).withHour(hour).withMinute(minute)
    }

    @Test
    fun `unconfigured hours report UNKNOWN`() {
        val snapshot = calculator.computeSnapshot(OpeningHours.empty(), dateTimeFor(1, 10, 0))
        assertThat(snapshot.status).isEqualTo(OpenStatus.UNKNOWN)
        assertThat(snapshot.nextChangeAtEpochMillis).isNull()
    }

    @Test
    fun `simple range reports OPEN inside window`() {
        val hours = OpeningHours(
            days = listOf(DayHours(dayOfWeek = 1, ranges = listOf(TimeRange(9 * 60, 18 * 60)))) + (2..7).map { DayHours(it, isClosed = true) },
            isConfigured = true
        )
        val snapshot = calculator.computeSnapshot(hours, dateTimeFor(1, 12, 0))
        assertThat(snapshot.status).isEqualTo(OpenStatus.OPEN)
    }

    @Test
    fun `simple range reports CLOSED outside window`() {
        val hours = OpeningHours(
            days = listOf(DayHours(dayOfWeek = 1, ranges = listOf(TimeRange(9 * 60, 18 * 60)))) + (2..7).map { DayHours(it, isClosed = true) },
            isConfigured = true
        )
        val snapshot = calculator.computeSnapshot(hours, dateTimeFor(1, 20, 0))
        assertThat(snapshot.status).isEqualTo(OpenStatus.CLOSED)
    }

    @Test
    fun `closing soon threshold triggers near the end of a range`() {
        val hours = OpeningHours(
            days = listOf(DayHours(dayOfWeek = 1, ranges = listOf(TimeRange(9 * 60, 18 * 60)))) + (2..7).map { DayHours(it, isClosed = true) },
            isConfigured = true
        )
        val snapshot = calculator.computeSnapshot(hours, dateTimeFor(1, 17, 45), closingSoonThresholdMinutes = 30)
        assertThat(snapshot.status).isEqualTo(OpenStatus.CLOSING_SOON)
        assertThat(snapshot.minutesUntilChange).isEqualTo(15)
    }

    @Test
    fun `overnight range crossing midnight is open after midnight`() {
        // 22:00 -> 02:00 on Friday should still be open at 01:00 on Saturday.
        val hours = OpeningHours(
            days = (1..7).map { day ->
                if (day == 5) DayHours(dayOfWeek = 5, ranges = listOf(TimeRange(22 * 60, 2 * 60))) else DayHours(day, isClosed = true)
            },
            isConfigured = true
        )
        val saturdayOneAm = dateTimeFor(6, 1, 0)
        val snapshot = calculator.computeSnapshot(hours, saturdayOneAm)
        assertThat(snapshot.status).isEqualTo(OpenStatus.OPEN)
    }

    @Test
    fun `open 24 hours never reports CLOSED`() {
        val hours = OpeningHours(days = (1..7).map { DayHours(it, isOpen24Hours = true) }, isConfigured = true)
        val snapshot = calculator.computeSnapshot(hours, dateTimeFor(3, 3, 30))
        assertThat(snapshot.status).isAnyOf(OpenStatus.OPEN, OpenStatus.CLOSING_SOON)
    }

    @Test
    fun `entirely closed week reports CLOSED with no next change`() {
        val hours = OpeningHours(days = (1..7).map { DayHours(it, isClosed = true) }, isConfigured = true)
        val snapshot = calculator.computeSnapshot(hours, dateTimeFor(2, 10, 0))
        assertThat(snapshot.status).isEqualTo(OpenStatus.CLOSED)
        assertThat(snapshot.nextChangeAtEpochMillis).isNull()
    }

    @Test
    fun `opening soon threshold triggers before the next open range`() {
        val hours = OpeningHours(
            days = listOf(DayHours(dayOfWeek = 2, ranges = listOf(TimeRange(9 * 60, 18 * 60)))) +
                (listOf(1, 3, 4, 5, 6, 7).map { DayHours(it, isClosed = true) }),
            isConfigured = true
        )
        val snapshot = calculator.computeSnapshot(hours, dateTimeFor(2, 8, 45), closingSoonThresholdMinutes = 30)
        assertThat(snapshot.status).isEqualTo(OpenStatus.OPENING_SOON)
        assertThat(snapshot.minutesUntilChange).isEqualTo(15)
    }

    @Test
    fun `toRemainingDuration splits minutes into days hours minutes`() {
        val duration = calculator.toRemainingDuration(2 * 24 * 60 + 3 * 60 + 15L)
        assertThat(duration.days).isEqualTo(2)
        assertThat(duration.hours).isEqualTo(3)
        assertThat(duration.minutes).isEqualTo(15)
    }

    @Test
    fun `multiple ranges in a day handle a lunch break correctly`() {
        val hours = OpeningHours(
            days = listOf(
                DayHours(dayOfWeek = 4, ranges = listOf(TimeRange(9 * 60, 12 * 60), TimeRange(13 * 60, 18 * 60)))
            ) + listOf(1, 2, 3, 5, 6, 7).map { DayHours(it, isClosed = true) },
            isConfigured = true
        )
        val duringLunch = calculator.computeSnapshot(hours, dateTimeFor(4, 12, 30))
        assertThat(duringLunch.status).isEqualTo(OpenStatus.CLOSED)

        val afternoon = calculator.computeSnapshot(hours, dateTimeFor(4, 14, 0))
        assertThat(afternoon.status).isEqualTo(OpenStatus.OPEN)
    }
}
