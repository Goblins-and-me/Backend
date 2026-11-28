package com.shop.SweetStory.owner.calendar.controller;

import com.shop.SweetStory.catalog.generalDto.ActionMessageResponse;
import com.shop.SweetStory.owner.calendar.dto.CreateDayOffRequest;
import com.shop.SweetStory.owner.calendar.dto.DayOffResponse;
import com.shop.SweetStory.owner.calendar.service.DayOffService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/days-off")
public class AdminCalendarController {

    private final DayOffService dayOffService;

    public AdminCalendarController(DayOffService dayOffService) {
        this.dayOffService = dayOffService;
    }

    @PostMapping
    public ResponseEntity<DayOffResponse> addDayOff(@Valid @RequestBody CreateDayOffRequest request) {
        DayOffResponse created = dayOffService.addDayOff(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ActionMessageResponse> removeDayOffById(@PathVariable Long id) {
        return ResponseEntity.ok(dayOffService.removeDayOffById(id));
    }

    @DeleteMapping("/by-date/{date}")
    public ResponseEntity<ActionMessageResponse> removeDayOffByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(dayOffService.removeDayOffByDate(date));
    }
}