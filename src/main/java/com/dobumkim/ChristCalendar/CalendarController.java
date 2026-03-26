package com.dobumkim.ChristCalendar;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.*;

@RestController
@RequiredArgsConstructor
public class CalendarController {
    private final ChurchCalendarService churchCalendarService;

    @GetMapping(value = "/calendar.ics", produces = "text/calendar;charset=UTF-8")
    public String getCalendar() {
        return churchCalendarService.getCalendar();
    }
}
