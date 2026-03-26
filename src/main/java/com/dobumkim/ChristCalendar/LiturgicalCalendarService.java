package com.dobumkim.ChristCalendar;

import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class LiturgicalCalendarService {

    /**
     * 주어진 연도의 부활절(Easter Sunday) 날짜를 계산하여 반환합니다.
     * Meeus/Jones/Butcher 알고리즘(그레고리력)을 사용합니다.
     */
    public LocalDate getEasterDate(int year) {
        int a = year % 19;
        int b = year / 100;
        int c = year % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int month = (h + l - 7 * m + 114) / 31;
        int day = ((h + l - 7 * m + 114) % 31) + 1;

        return LocalDate.of(year, month, day);
    }

    // n번째 주일 계산 (맥추/추수감사주일용)
    public LocalDate getNthSunday(int year, int month, int n) {
        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate firstSunday = firstDay
                .with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));
        return firstSunday.plusWeeks(n - 1);
    }
}