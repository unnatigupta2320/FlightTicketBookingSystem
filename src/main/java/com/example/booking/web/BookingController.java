package com.example.booking.web;

import com.example.booking.model.Booking;
import com.example.booking.service.BookingService;
import com.example.booking.web.dto.BookingResponse;
import com.example.booking.web.dto.CreateBookingRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody CreateBookingRequest request,
                                                        UriComponentsBuilder uriBuilder) {
        Booking booking = bookingService.book(
                request.flightNumber(), request.passengerName(), request.seats());
        URI location = uriBuilder.path("/api/bookings/{id}")
                .buildAndExpand(booking.bookingId())
                .toUri();
        return ResponseEntity.created(location).body(BookingResponse.from(booking));
    }
}
