package com.dobumkim.ChristCalendar;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.model.property.XProperty;
import lombok.*;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class CalendarController {

    private final LiturgicalCalendarService liturgicalCalendarService;

    @GetMapping(value = "/calendar.ics", produces = "text/calendar;charset=UTF-8")
    public String getCalendar() {
        Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//DobumKim//ChurchCalendar//KO"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(new XProperty("X-WR-CALNAME", "예장통합 절기 캘린더"));
        calendar.getProperties().add(new XProperty("X-WR-TIMEZONE", "Asia/Seoul"));

        int currentYear = LocalDate.now().getYear();

        // 올해와 내년 절기를 모두 생성 (사용자가 미리 확인할 수 있게)
        for (int year : new int[] { currentYear, currentYear + 1 }) {
            addHolidays(calendar, year);
        }

        return calendar.toString();
    }

    private void addHolidays(Calendar calendar, int year) {
        LocalDate easter = liturgicalCalendarService.getEasterDate(year);

        // 1. 부활절 기준 절기들
        addEvent(calendar, "부활주일", easter, "easter-" + year);
        addEvent(calendar, "재의 수요일(사순절 시작)", easter.minusDays(46), "ash-wednesday-" + year);
        addEvent(calendar, "종려주일", easter.minusDays(7), "palm-sunday-" + year);
        addEvent(calendar, "성령강림주일", easter.plusDays(49), "pentecost-" + year);

        // 2. 고정 날짜 및 요일 기준 절기들
        addEvent(calendar, "맥추감사주일", liturgicalCalendarService.getNthSunday(year, 7, 1), "macchu-" + year);
        addEvent(calendar, "추수감사주일", liturgicalCalendarService.getNthSunday(year, 11, 3), "thanksgiving-" + year);
        addEvent(calendar, "성탄절", LocalDate.of(year, 12, 25), "christmas-" + year);
    }

    private void addEvent(Calendar calendar, String summary, LocalDate date, String uidKey) {
        VEvent event = new VEvent(
                new net.fortuna.ical4j.model.Date(java.sql.Date.valueOf(date)),
                summary);
        event.getProperties().add(new Uid("pck-" + uidKey));
        calendar.getComponents().add(event);
    }
}
