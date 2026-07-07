package com.example.booking.repository;

import com.example.booking.model.Flight;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory store of flights keyed by flight number. */
@Repository
public class FlightRepository {

    private final ConcurrentHashMap<String, Flight> flights = new ConcurrentHashMap<>();

    public Optional<Flight> findByFlightNumber(String flightNumber) {
        return Optional.ofNullable(flights.get(flightNumber));
    }

    public boolean exists(String flightNumber) {
        return flights.containsKey(flightNumber);
    }

    /**
     * Save the flight only if one with the same number does not already exist.
     *
     * @return {@code true} if stored, {@code false} if the flight number was taken.
     */
    public boolean saveIfAbsent(Flight flight) {
        return flights.putIfAbsent(flight.getFlightNumber(), flight) == null;
    }
}
