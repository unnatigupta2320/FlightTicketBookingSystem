package com.example.booking.web.dto;

import com.example.booking.model.Booking;

import java.time.Instant;

public record BookingResponse(
        String bookingId,
        String flightNumber,
        String passengerName,
        int seats,
        Instant createdAt) {

    public static BookingResponse from(Booking booking) {
        return new BookingResponse(
                booking.bookingId(),
                booking.flightNumber(),
                booking.passengerName(),
                booking.seats(),
                booking.createdAt());
    }
}
