package com.example.booking.exception;

/** Thrown when registering a flight whose number is already in use. */
public class FlightAlreadyExistsException extends RuntimeException {
    public FlightAlreadyExistsException(String flightNumber) {
        super("Flight already exists: " + flightNumber);
    }
}
