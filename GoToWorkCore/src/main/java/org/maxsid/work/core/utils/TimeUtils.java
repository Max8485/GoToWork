package org.maxsid.work.core.utils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TimeUtils {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static LocalTime parseTime(String timeString) throws DateTimeParseException {
        return LocalTime.parse(timeString, TIME_FORMATTER);
    }

    public static String formatTime(LocalTime time) {
        return time.format(TIME_FORMATTER);
    }

    public static String calculateDepartureTime(String arrivalTime, long travelMinutes) {
        LocalTime arrival = parseTime(arrivalTime);
        LocalTime departure = arrival
                .minusMinutes(travelMinutes)
                .minusMinutes(30); // 30 минут буфер

        return formatTime(departure);
    }

    public static boolean isWeekday() {
        java.time.DayOfWeek dayOfWeek = java.time.LocalDate.now().getDayOfWeek();
        return dayOfWeek != java.time.DayOfWeek.SATURDAY && dayOfWeek != java.time.DayOfWeek.SUNDAY;
    }
}
