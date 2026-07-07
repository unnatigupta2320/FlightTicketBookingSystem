package com.example.booking.exception;

/** Thrown when cancelling a booking id that does not exist. */
public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(String bookingId) {
        super("Booking not found: " + bookingId);
    }
}
