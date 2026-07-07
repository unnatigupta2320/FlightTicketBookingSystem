package com.example.booking.model;

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
     *
     * @return {@code true} if the seats were reserved, {@code false} if there was
     *         not enough remaining capacity (no state change in that case).
     */
    public synchronized boolean tryReserve(int seats) {
        if (seats > totalSeats - bookedSeats) {
            return false;
        }
        bookedSeats += seats;
        return true;
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
