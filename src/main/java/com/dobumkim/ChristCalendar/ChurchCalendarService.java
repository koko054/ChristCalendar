package com.dobumkim.ChristCalendar;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.model.property.XProperty;
import org.springframework.cache.annotation.Cacheable;

@Service
public class ChurchCalendarService {

        @Cacheable(value = "churchCalendar", key = "T(java.time.LocalDate).now().getYear()")
        public String getCalendar() {
                Calendar calendar = new Calendar();
                calendar.getProperties().add(new ProdId("-//DobumKim//ChurchCalendar//KO"));
                calendar.getProperties().add(Version.VERSION_2_0);
                calendar.getProperties().add(new XProperty("X-WR-CALNAME", "개신교 절기 캘린더"));
                calendar.getProperties().add(new XProperty("X-WR-TIMEZONE", "Asia/Seoul"));

                int currentYear = LocalDate.now().getYear();
                int startYear = currentYear - 1;
                int endYear = currentYear + 10;

                for (int year = startYear; year <= endYear; year++) {
                        addHolidays(calendar, year);
                }

                return calendar.toString();
        }

        private void addHolidays(Calendar calendar, int year) {
                LocalDate easter = getEasterDate(year);
                LocalDate advent = getAdventStartDate(year);

                // 1. 부활절 기준 절기들
                addEvent(calendar, "부활주일", easter, "easter-" + year, "죽음을 이기시고 다시 사신 예수 그리스도를 기뻐하는 기독교 최고의 축제입니다.");
                addEvent(calendar, "재의 수요일(사순절 시작)", easter.minusDays(46), "ash-wednesday-" + year,
                                "사순절이 시작되는 첫날입니다. 재를 머리에 얹고 우리가 흙에서 왔음을 기억하며 철저히 회개하는 날입니다.");
                addEvent(calendar, "종려주일", easter.minusDays(7), "palm-sunday-" + year,
                                "고난주간이 시작되는 주일입니다. 예수님이 나귀를 타고 예루살렘에 입성하실 때 군중들이 종려나무 가지를 흔들며 환호한 것을 기념합니다.");
                addEvent(calendar, "성령강림주일", easter.plusDays(49), "pentecost-" + year,
                                "부활 후 50일째 되는 날, 약속하신 성령님이 마가의 다락방에 임하심을 기념합니다. 교회의 탄생일이기도 합니다.");

                // 2. 고정 날짜 및 요일 기준 절기들
                addEvent(calendar, "주현절", LocalDate.of(year, 1, 6), "epiphany-" + year,
                                "예수님이 세상의 빛으로 나타나심을 기리는 절기입니다. 동방박사들이 아기 예수를 찾아온 것을 기념하며, 그리스도가 온 인류의 구원자이심을 선포합니다.");
                addEvent(calendar, "맥추감사주일", getNthSunday(year, 7, 1), "macchu-" + year,
                                "보리 수확을 마친 후, 한 해의 절반 동안 지켜주신 하나님의 은혜에 감사하는 절기입니다.");
                addEvent(calendar, "추수감사주일", getNthSunday(year, 11, 3),
                                "thanksgiving-" + year,
                                "한 해 동안 베풀어 주신 모든 수확과 은혜에 감사하며 드리는 절기입니다.");
                addEvent(calendar, "성탄절", LocalDate.of(year, 12, 25), "christmas-" + year,
                                "인류를 구원하기 위해 이 땅에 오신 예수 그리스도의 탄생을 기념하는 기쁜 날입니다.");

                // 3. 대림절
                for (int i = 0; i < 4; i++) {
                        addEvent(calendar, "대림절 " + (i + 1) + "번째 주일", advent.plusWeeks(i),
                                        "advent-" + (i + 1) + "-" + year,
                                        "성탄절 전 4주간의 기간입니다. 아기 예수의 오심을 기다리며, 또한 다시 오실 주님을 소망하며 기다리는 절기입니다.");
                }
        }

        // addEvent 메소드 개선 버전
        private void addEvent(Calendar calendar, String summary, LocalDate date, String uidKey, String description) {
                String dateStr = date.format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
                net.fortuna.ical4j.model.Date icalDate;
                try {
                        icalDate = new net.fortuna.ical4j.model.Date(dateStr);
                } catch (java.text.ParseException e) {
                        throw new RuntimeException("Failed to parse date", e);
                }

                VEvent event = new VEvent(icalDate, summary);

                // 2. 필수 프로퍼티 설정
                event.getProperties().add(new Uid("pck-" + uidKey));

                // 3. 상세 설명 추가 (줄바꿈 \n 활용)
                if (description != null) {
                        // \\n 대신 \n (실제 개행문자)를 사용하세요.
                        // ical4j가 .ics 파일로 만들 때 자동으로 이를 텍스트 '\n'으로 변환해줍니다.
                        String formattedDesc = description.replace(". ", ".\n");
                        event.getProperties().add(new net.fortuna.ical4j.model.property.Description(formattedDesc));
                }

                calendar.getComponents().add(event);
        }

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

        // 대림절 시작일 (성탄절 전 4번째 주일)
        public LocalDate getAdventStartDate(int year) {
                LocalDate christmas = LocalDate.of(year, 12, 25);
                // 성탄절 직전 주일부터 시작하여 4주 전 주일을 계산
                LocalDate firstSundayBeforeChristmas = christmas
                                .with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.SUNDAY));
                return firstSundayBeforeChristmas.minusWeeks(3);
        }
}