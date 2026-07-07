package com.example.booking.model;

import java.time.Instant;

/** An immutable confirmed booking for a number of seats on a flight. */
public record Booking(
        String bookingId,
        String flightNumber,
        String passengerName,
        int seats,
        Instant createdAt) {
}
