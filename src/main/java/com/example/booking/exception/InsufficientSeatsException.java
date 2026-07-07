package com.example.booking.exception;

/** Thrown when a booking requests more seats than a flight has remaining. */
public class InsufficientSeatsException extends RuntimeException {
    public InsufficientSeatsException(String flightNumber, int requested, int available) {
        super("Not enough seats on flight " + flightNumber
                + ": requested " + requested + ", available " + available);
    }
}
