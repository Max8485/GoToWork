package org.maxsid.work.core.service.impl;

import org.maxsid.work.core.service.TimeZoneService;
import org.maxsid.work.core.utils.RussianTimeZone;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;

//@Service
public class TimeZoneServiceImpl{

//    /**
//     * Проверяет, является ли текущий день будним в указанном часовом поясе
//     */
//    public boolean isWeekdayInTimeZone(String zoneId) {
//        try {
//            ZoneId zone = ZoneId.of(zoneId);
//            ZonedDateTime nowInZone = ZonedDateTime.now(zone);
//            DayOfWeek dayOfWeek = nowInZone.getDayOfWeek();
//
//            return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
//        } catch (Exception e) {
//            System.err.println("Error checking weekday for timezone " + zoneId + ": " + e.getMessage());
//            return isWeekdaySystem();
//        }
//    }
//
//    /**
//     * Получает текущее время в указанном часовом поясе
//     */
//    public String getCurrentTimeInTimeZone(String zoneId) {
//        try {
//            ZoneId zone = ZoneId.of(zoneId);
//            ZonedDateTime nowInZone = ZonedDateTime.now(zone);
//            return nowInZone.format(DateTimeFormatter.ofPattern("HH:mm"));
//        } catch (Exception e) {
//            System.err.println("Error getting current time for timezone " + zoneId + ": " + e.getMessage());
//            return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
//        }
//    }
//
//    /**
//     * Получает полную информацию о часовом поясе
//     */
//    public String getTimeZoneInfo(String zoneId) {
//        try {
//            RussianTimeZone russianZone = RussianTimeZone.fromZoneId(zoneId);
//            ZoneId zone = ZoneId.of(zoneId);
//            ZonedDateTime nowInZone = ZonedDateTime.now(zone);
//
//            return String.format("%s\nТекущее время: %s\nДень недели: %s",
//                    russianZone.getDisplayName(),
//                    nowInZone.format(DateTimeFormatter.ofPattern("HH:mm, dd.MM.yyyy")),
//                    getRussianDayOfWeek(nowInZone.getDayOfWeek())
//            );
//        } catch (Exception e) {
//            return "Московское время (UTC+3)";
//        }
//    }
//
//    /**
//     * Получает русское название дня недели
//     */
//    private String getRussianDayOfWeek(DayOfWeek dayOfWeek) {
//        switch (dayOfWeek) {
//            case MONDAY: return "Понедельник";
//            case TUESDAY: return "Вторник";
//            case WEDNESDAY: return "Среда";
//            case THURSDAY: return "Четверг";
//            case FRIDAY: return "Пятница";
//            case SATURDAY: return "Суббота";
//            case SUNDAY: return "Воскресенье";
//            default: return "Неизвестно";
//        }
//    }
//
//    private boolean isWeekdaySystem() {
//        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
//        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
//    }
//
//    /**
//     * Получает список всех российских часовых поясов
//     */
//    public java.util.List<String> getAllRussianTimeZones() {
//        return RussianTimeZone.getAllTimeZoneNames();
//    }
}
