package com.example.booking.repository;

import com.example.booking.model.Booking;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory store of bookings keyed by booking id. */
@Repository
public class BookingRepository {

    private final ConcurrentHashMap<String, Booking> bookings = new ConcurrentHashMap<>();

    public Booking save(Booking booking) {
        bookings.put(booking.bookingId(), booking);
        return booking;
    }

    public Optional<Booking> findById(String bookingId) {
        return Optional.ofNullable(bookings.get(bookingId));
    }

    public Optional<Booking> remove(String bookingId) {
        return Optional.ofNullable(bookings.remove(bookingId));
    }
}
