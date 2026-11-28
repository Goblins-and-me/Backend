package com.shop.SweetStory.owner.calendar.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateDayOffRequest(
        @NotNull
        LocalDate date
) {
}
