package com.shop.SweetStory.owner.calendar.controller;


import com.shop.SweetStory.owner.calendar.dto.DayOffResponse;
import com.shop.SweetStory.owner.calendar.service.DayOffService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/days-off")
public class CalendarController {

       private final DayOffService dayOffService;

        public CalendarController(DayOffService dayOffService) {
            this.dayOffService = dayOffService;
        }

        @GetMapping
        public ResponseEntity<List<DayOffResponse>> getDaysOff(
                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
        ) {
            return ResponseEntity.ok(dayOffService.getDaysOff(from, to));
        }

}
