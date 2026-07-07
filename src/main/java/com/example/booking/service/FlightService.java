package com.example.booking.service;

import com.example.booking.exception.FlightAlreadyExistsException;
import com.example.booking.exception.FlightNotFoundException;
import com.example.booking.model.Flight;
import com.example.booking.repository.FlightRepository;
import org.springframework.stereotype.Service;

@Service
public class FlightService {

    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    /**
     * Register a new flight (inventory provisioning).
     *
     * @throws FlightAlreadyExistsException if the flight number is already taken.
     */
    public Flight registerFlight(String flightNumber, int totalSeats) {
        Flight flight = new Flight(flightNumber, totalSeats);
        if (!flightRepository.saveIfAbsent(flight)) {
            throw new FlightAlreadyExistsException(flightNumber);
        }
        return flight;
    }

    /**
     * @throws FlightNotFoundException if the flight number is unknown.
     */
    public Flight getFlight(String flightNumber) {
        return flightRepository.findByFlightNumber(flightNumber)
                .orElseThrow(() -> new FlightNotFoundException(flightNumber));
    }
}
