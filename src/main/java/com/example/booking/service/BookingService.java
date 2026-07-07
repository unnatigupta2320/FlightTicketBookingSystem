package com.example.booking.service;

import com.example.booking.exception.FlightNotFoundException;
import com.example.booking.exception.InsufficientSeatsException;
import com.example.booking.model.Booking;
import com.example.booking.model.Flight;
import com.example.booking.repository.BookingRepository;
import com.example.booking.repository.FlightRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class BookingService {

    private final FlightRepository flightRepository;
    private final BookingRepository bookingRepository;

    public BookingService(FlightRepository flightRepository, BookingRepository bookingRepository) {
        this.flightRepository = flightRepository;
        this.bookingRepository = bookingRepository;
    }

    /**
     * Book {@code seats} on a known flight. Seat reservation is atomic per flight,
     * so overbooking cannot occur even under concurrent requests.
     *
     * @throws FlightNotFoundException    if the flight number is unknown.
     * @throws InsufficientSeatsException if not enough seats remain.
     */
    public Booking book(String flightNumber, String passengerName, int seats) {
        Flight flight = flightRepository.findByFlightNumber(flightNumber)
                .orElseThrow(() -> new FlightNotFoundException(flightNumber));

        if (!flight.tryReserve(seats)) {
            throw new InsufficientSeatsException(flightNumber, seats, flight.getAvailableSeats());
        }

        Booking booking = new Booking(
                UUID.randomUUID().toString(),
                flightNumber,
                passengerName,
                seats,
                Instant.now());
        return bookingRepository.save(booking);
    }
}
