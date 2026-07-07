package com.example.booking.model;

import com.example.booking.exception.InsufficientSeatsException;

/**
 * A flight with a fixed seat inventory. Seat reservation is guarded by intrinsic
 * locking on the instance so concurrent bookings can never oversell the flight.
 */
public class Flight {

    private final String flightNumber;
    private final int totalSeats;
    private int bookedSeats;

    public Flight(String flightNumber, int totalSeats) {
        this.flightNumber = flightNumber;
        this.totalSeats = totalSeats;
        this.bookedSeats = 0;
    }

    /**
     * Atomically reserve {@code seats} if enough remain.
     * We should update the remaining seats also inside the block
     * throws InsufficientSeatsException if there are not enough seats available
     */
     public synchronized void reserve(int seats) {
        int available = totalSeats - bookedSeats;
        if (seats > available) {
            throw new InsufficientSeatsException(flightNumber, seats, available);
        }
        bookedSeats += seats;
    }

    /** Release previously reserved seats back into inventory. */
    public synchronized void release(int seats) {
        bookedSeats = Math.max(0, bookedSeats - seats);
    }

    public synchronized int getAvailableSeats() {
        return totalSeats - bookedSeats;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public synchronized int getBookedSeats() {
        return bookedSeats;
    }
}
