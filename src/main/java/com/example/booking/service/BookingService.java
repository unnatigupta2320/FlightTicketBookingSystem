package com.example.booking.service;

import com.example.booking.exception.BookingNotFoundException;
import com.example.booking.exception.FlightNotFoundException;
import com.example.booking.exception.InsufficientSeatsException;
import com.example.booking.model.Booking;
import com.example.booking.model.Flight;
import com.example.booking.repository.BookingRepository;
import com.example.booking.repository.FlightRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Service
public class BookingService {

    private final FlightRepository flightRepository;
    private final BookingRepository bookingRepository;
    private final Clock clock;

    public BookingService(FlightRepository flightRepository,
                          BookingRepository bookingRepository,
                          Clock clock) {
        this.flightRepository = flightRepository;
        this.bookingRepository = bookingRepository;
        this.clock = clock;
    }

    /**
     * Book {@code seats} on a known flight. Seat reservation is atomic per flight,
     * so overbooking cannot occur even under concurrent requests.
     *
     * @throws FlightNotFoundException    if the flight number is unknown.
     * @throws InsufficientSeatsException if not enough seats remain (thrown by Flight.reserve).
     */
    public Booking book(String flightNumber, String passengerName, int seats) {
        String fn = FlightService.normalize(flightNumber);
        Flight flight = flightRepository.findByFlightNumber(fn)
                .orElseThrow(() -> new FlightNotFoundException(fn));

        flight.reserve(seats); // atomically throws InsufficientSeatsException if full

        Booking booking = new Booking(
                UUID.randomUUID().toString(),
                fn,
                passengerName,
                seats,
                Instant.now(clock));
        return bookingRepository.save(booking);
    }

    /**
     * Cancel a booking and release its seats back to the flight. Removal is atomic,
     * so a concurrent double-cancel releases the seats at most once.
     *
     * @throws BookingNotFoundException if the booking id is unknown.
     */
    public void cancel(String bookingId) {
        Booking booking = bookingRepository.remove(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));
        flightRepository.findByFlightNumber(booking.flightNumber())
                .ifPresent(flight -> flight.release(booking.seats()));
    }
}
