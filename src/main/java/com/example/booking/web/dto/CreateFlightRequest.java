package com.example.booking.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateFlightRequest(
        @NotBlank String flightNumber,
        @NotNull @Min(1) Integer totalSeats) {
}
