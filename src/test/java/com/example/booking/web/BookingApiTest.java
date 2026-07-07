package com.example.booking.web;

import com.example.booking.service.FlightService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookingApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FlightService flightService;

    private String json(String flightNumber, String passenger, Integer seats) {
        StringBuilder sb = new StringBuilder("{");
        if (flightNumber != null) sb.append("\"flightNumber\":\"").append(flightNumber).append("\",");
        if (passenger != null) sb.append("\"passengerName\":\"").append(passenger).append("\",");
        if (seats != null) sb.append("\"seats\":").append(seats).append(",");
        if (sb.charAt(sb.length() - 1) == ',') sb.setLength(sb.length() - 1);
        return sb.append("}").toString();
    }

    @Test
    void bookingSucceedsAndReturnsCreatedWithLocation() throws Exception {
        flightService.registerFlight("HAPPY1", 5);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("HAPPY1", "Alice", 2)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", notNullValue()))
                .andExpect(jsonPath("$.bookingId", notNullValue()))
                .andExpect(jsonPath("$.flightNumber").value("HAPPY1"))
                .andExpect(jsonPath("$.seats").value(2));
    }

    @Test
    void bookingUnknownFlightReturns404() throws Exception {
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("NOPE999", "Dan", 1)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void overbookingReturns409() throws Exception {
        flightService.registerFlight("FULL1", 1);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("FULL1", "First", 1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("FULL1", "Second", 1)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void invalidSeatsReturns400() throws Exception {
        flightService.registerFlight("BADREQ1", 5);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("BADREQ1", "Eve", 0)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.seats", notNullValue()));
    }

    @Test
    void missingPassengerNameReturns400() throws Exception {
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("AA100", null, 1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.passengerName", notNullValue()));
    }

    @Test
    void registerFlightSucceedsThenDuplicateReturns409() throws Exception {
        mockMvc.perform(post("/api/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"flightNumber\":\"REG1\",\"totalSeats\":10}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.availableSeats").value(10));

        mockMvc.perform(post("/api/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"flightNumber\":\"REG1\",\"totalSeats\":10}"))
                .andExpect(status().isConflict());
    }
}
