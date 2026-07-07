package com.example.booking.service;

import com.example.booking.exception.InsufficientSeatsException;
import com.example.booking.model.Flight;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BookingConcurrencyTest {

    @Autowired
    private FlightService flightService;

    @Autowired
    private BookingService bookingService;

    /**
     * Fire many more concurrent single-seat bookings than there are seats and
     * assert the flight is never oversold: exactly capacity bookings succeed and
     * bookedSeats never exceeds totalSeats.
     */
    @Test
    void concurrentBookingsNeverOverbook() throws Exception {
        int capacity = 100;
        int attempts = 300;
        Flight flight = flightService.registerFlight("CONC1", capacity);

        ExecutorService pool = Executors.newFixedThreadPool(32);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger success = new AtomicInteger();
        AtomicInteger rejected = new AtomicInteger();

        for (int i = 0; i < attempts; i++) {
            final int idx = i;
            pool.submit(() -> {
                try {
                    start.await();
                    bookingService.book("CONC1", "P" + idx, 1);
                    success.incrementAndGet();
                } catch (InsufficientSeatsException e) {
                    rejected.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        start.countDown();
        pool.shutdown();
        assertThat(pool.awaitTermination(30, TimeUnit.SECONDS)).isTrue();

        assertThat(success.get()).isEqualTo(capacity);
        assertThat(rejected.get()).isEqualTo(attempts - capacity);
        assertThat(flight.getBookedSeats()).isEqualTo(capacity);
        assertThat(flight.getAvailableSeats()).isZero();
    }
}
