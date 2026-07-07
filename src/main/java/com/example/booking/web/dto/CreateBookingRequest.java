package com.example.booking.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

/** We should also validate the flight number and passenger name, and the seats should be between 1 and 9. */
public record CreateBookingRequest(
        @NotBlank @Size(max = 10) String flightNumber,
        @NotBlank @Size(max = 100) String passengerName,
        @NotNull @Min(1) @Max(9) Integer seats) {
}
