package quartet.server.core.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.*;
import java.time.temporal.TemporalAdjusters;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateUtils {

    public static Instant now() {
        return ZonedDateTime.now(ZoneId.systemDefault()).toInstant();
    }

    public static LocalDate today() {
        return LocalDate.now(ZoneId.systemDefault());
    }

    public static Instant toInstant(final LocalDate date) {
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant();
    }

    public static LocalDate toLocalDate(final Instant instant) {
        return LocalDate.ofInstant(instant, ZoneId.systemDefault());
    }

    public static Instant toInstant(final LocalDate date, final int hour, final int minute, final int second) {
        return LocalDateTime.of(date, LocalTime.of(hour, minute, second))
                .atZone(ZoneId.systemDefault())
                .toInstant();
    }

    public static Instant getDateBefore(final LocalDate baseDate, final long years, final long months, final long days) {
        return baseDate.minusYears(years)
                .minusMonths(months)
                .minusDays(days)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();
    }

    public static Instant getDateAfter(final LocalDate baseDate, final long years, final long months, final long days) {
        return baseDate.plusYears(years)
                .plusMonths(months)
                .plusDays(days)
                .atTime(LocalTime.MAX)
                .atZone(ZoneId.systemDefault())
                .toInstant();
    }

    public static Instant getDateBeforeNow(final long years, final long months, final long days) {
        return getDateBefore(today(), years, months, days);
    }

    public static Instant getDateAfterNow(final long years, final long months, final long days) {
        return getDateAfter(today(), years, months, days);
    }

    public static Instant getTodayStart() {
        return today()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();
    }

    public static Instant getFirstDayOfMonth(final LocalDate date) {
        return date.with(TemporalAdjusters.firstDayOfMonth())
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();
    }

    public static Instant getLastDayOfMonth(final LocalDate date) {
        return date.with(TemporalAdjusters.lastDayOfMonth())
                .atTime(LocalTime.MAX)
                .atZone(ZoneId.systemDefault())
                .toInstant();
    }

    public static Instant getFirstDayOfMonth(final Instant instant) {
        return getFirstDayOfMonth(toLocalDate(instant));
    }

    public static Instant getLastDayOfMonth(final Instant instant) {
        return getLastDayOfMonth(toLocalDate(instant));
    }

    public static Instant getFirstDayOfCurrentMonth() {
        return getFirstDayOfMonth(today());
    }

    public static Instant getLastDayOfCurrentMonth() {
        return getLastDayOfMonth(today());
    }
}