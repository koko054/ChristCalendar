package com.dobumkim.ChristCalendar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class ChristCalendarApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChristCalendarApplication.class, args);
	}

}
