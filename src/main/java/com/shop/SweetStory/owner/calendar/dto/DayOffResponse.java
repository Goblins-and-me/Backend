package com.shop.SweetStory.owner.calendar.dto;

import java.time.LocalDate;

public record DayOffResponse (
        Long id,
        LocalDate date
) {
}
