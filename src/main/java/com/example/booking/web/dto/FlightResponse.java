package com.example.booking.web.dto;

import com.example.booking.model.Flight;

public record FlightResponse(
        String flightNumber,
        int totalSeats,
        int availableSeats) {

    public static FlightResponse from(Flight flight) {
        return new FlightResponse(
                flight.getFlightNumber(),
                flight.getTotalSeats(),
                flight.getAvailableSeats());
    }
}
