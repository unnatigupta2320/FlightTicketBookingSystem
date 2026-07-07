package com.example.booking.web;

import com.example.booking.model.Flight;
import com.example.booking.service.FlightService;
import com.example.booking.web.dto.CreateFlightRequest;
import com.example.booking.web.dto.FlightResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @PostMapping
    public ResponseEntity<FlightResponse> registerFlight(@Valid @RequestBody CreateFlightRequest request,
                                                         UriComponentsBuilder uriBuilder) {
        Flight flight = flightService.registerFlight(request.flightNumber(), request.totalSeats());
        URI location = uriBuilder.path("/api/flights/{flightNumber}")
                .buildAndExpand(flight.getFlightNumber())
                .toUri();
        return ResponseEntity.created(location).body(FlightResponse.from(flight));
    }
}
