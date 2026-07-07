package com.example.booking.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateBookingRequest(
        @NotBlank String flightNumber,
        @NotBlank String passengerName,
        @NotNull @Min(1) Integer seats) {
}
