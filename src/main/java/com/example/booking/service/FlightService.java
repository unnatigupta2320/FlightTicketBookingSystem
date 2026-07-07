package com.example.booking.service;

import com.example.booking.exception.FlightAlreadyExistsException;
import com.example.booking.exception.FlightNotFoundException;
import com.example.booking.model.Flight;
import com.example.booking.repository.FlightRepository;
import org.springframework.stereotype.Service;
import java.util.Locale;

@Service
public class FlightService {

    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }
    /** We should also normalize the flight number, incase a person enters flight1 or FLIGHT1, it should be same. */
    public static String normalize(String flightNumber) {
        return flightNumber == null ? null : flightNumber.trim().toUpperCase(Locale.ROOT);
    }

    /**
     * Register a new flight (inventory provisioning).
     *
     * @throws FlightAlreadyExistsException if the flight number is already taken.
     */
    public Flight registerFlight(String flightNumber, int totalSeats) {
        Flight flight = new Flight(normalize(flightNumber), totalSeats);
        if (!flightRepository.saveIfAbsent(flight)) {
            throw new FlightAlreadyExistsException(flightNumber);
        }
        return flight;
    }

    /**
     * @throws FlightNotFoundException if the flight number is unknown.
     */
    public Flight getFlight(String flightNumber) {
        String normalizedFlightNumber = normalize(flightNumber);
        return flightRepository.findByFlightNumber(normalizedFlightNumber)
                .orElseThrow(() -> new FlightNotFoundException(flightNumber));
    }
}
