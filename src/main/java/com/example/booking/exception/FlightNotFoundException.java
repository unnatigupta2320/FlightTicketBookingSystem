package com.example.booking.exception;

/** Thrown when an operation references a flight number that does not exist. */
public class FlightNotFoundException extends RuntimeException {
    public FlightNotFoundException(String flightNumber) {
        super("Flight not found: " + flightNumber);
    }
}
