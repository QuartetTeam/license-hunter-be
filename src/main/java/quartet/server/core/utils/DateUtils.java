package quartet.server.core.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.*;
import java.time.temporal.TemporalAdjusters;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateUtils {
    private static final ZoneId TIME_ZONE = ZoneId.systemDefault();

    public static LocalDateTime now() {
        return LocalDateTime.now(TIME_ZONE);
    }

    public static LocalDate today() {
        return LocalDate.now(TIME_ZONE);
    }

    public static LocalDateTime toLocalDateTime(final LocalDate date) {
        return date.atStartOfDay();
    }

    public static LocalDate toLocalDate(final LocalDateTime dateTime) {
        return dateTime.toLocalDate();
    }

    public static LocalDateTime toLocalDateTime(final LocalDate date, final int hour, final int minute, final int second) {
        return LocalDateTime.of(date, LocalTime.of(hour, minute, second));
    }

    public static LocalDateTime getDateBefore(final LocalDate baseDate, final long years, final long months, final long days) {
        return baseDate.minusYears(years)
                .minusMonths(months)
                .minusDays(days)
                .atStartOfDay();
    }

    public static LocalDateTime getDateAfter(final LocalDate baseDate, final long years, final long months, final long days) {
        return baseDate.plusYears(years)
                .plusMonths(months)
                .plusDays(days)
                .atTime(LocalTime.MAX);
    }

    public static LocalDateTime getDateBeforeNow(final long years, final long months, final long days) {
        return getDateBefore(today(), years, months, days);
    }

    public static LocalDateTime getDateAfterNow(final long years, final long months, final long days) {
        return getDateAfter(today(), years, months, days);
    }

    public static LocalDateTime getTodayStart() {
        return today().atStartOfDay();
    }

    public static LocalDateTime getTodayEnd() {
        return today().atTime(LocalTime.MAX);
    }

    public static LocalDateTime getDayStart(final LocalDateTime dateTime) {
        return dateTime.toLocalDate().atStartOfDay();
    }

    public static LocalDateTime getDayEnd(final LocalDateTime dateTime) {
        return dateTime.toLocalDate().atTime(LocalTime.MAX);
    }

    public static LocalDateTime getFirstDayOfMonth(final LocalDate date) {
        return date.with(TemporalAdjusters.firstDayOfMonth())
                .atStartOfDay();
    }

    public static LocalDateTime getLastDayOfMonth(final LocalDate date) {
        return date.with(TemporalAdjusters.lastDayOfMonth())
                .atTime(LocalTime.MAX);
    }

    public static LocalDateTime getFirstDayOfMonth(final LocalDateTime dateTime) {
        return getFirstDayOfMonth(dateTime.toLocalDate());
    }

    public static LocalDateTime getLastDayOfMonth(final LocalDateTime dateTime) {
        return getLastDayOfMonth(dateTime.toLocalDate());
    }
}