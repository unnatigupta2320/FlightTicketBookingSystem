package com.example.booking.config;

import com.example.booking.service.FlightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/** Seeds a few flights at startup so the booking API has inventory to work against. */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final FlightService flightService;

    public DataSeeder(FlightService flightService) {
        this.flightService = flightService;
    }

    @Override
    public void run(String... args) {
        flightService.registerFlight("AA100", 2);
        flightService.registerFlight("BA200", 150);
        flightService.registerFlight("CA300", 50);
        log.info("Seeded flights: AA100 (2 seats), BA200 (150 seats), CA300 (50 seats)");
    }
}
