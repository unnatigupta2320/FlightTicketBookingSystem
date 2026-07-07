package com.example.booking.web;

import com.example.booking.service.FlightService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookingLifecycleTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FlightService flightService;

    @Test
    void availabilityReflectsBookingsAnd404ForUnknown() throws Exception {
        flightService.registerFlight("AVAIL1", 3);

        mockMvc.perform(get("/api/flights/AVAIL1/availability"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSeats").value(3))
                .andExpect(jsonPath("$.availableSeats").value(3));

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"flightNumber\":\"AVAIL1\",\"passengerName\":\"Amy\",\"seats\":2}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/flights/AVAIL1/availability"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableSeats").value(1));

        mockMvc.perform(get("/api/flights/UNKNOWN9/availability"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancellationReleasesSeatAndAllowsRebooking() throws Exception {
        flightService.registerFlight("CANCEL1", 1);

        String location = mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"flightNumber\":\"CANCEL1\",\"passengerName\":\"First\",\"seats\":1}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");

        // flight now full -> next booking rejected
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"flightNumber\":\"CANCEL1\",\"passengerName\":\"Second\",\"seats\":1}"))
                .andExpect(status().isConflict());

        // cancel the first booking -> 204
        mockMvc.perform(delete(location))
                .andExpect(status().isNoContent());

        // seat released -> booking now succeeds
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"flightNumber\":\"CANCEL1\",\"passengerName\":\"Third\",\"seats\":1}"))
                .andExpect(status().isCreated());
    }

    @Test
    void cancellingUnknownBookingReturns404() throws Exception {
        mockMvc.perform(delete("/api/bookings/does-not-exist"))
                .andExpect(status().isNotFound());
    }
}
