package com.example.booking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI flightBookingOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Flight Booking API")
                .description("Book seats on known flights. In-memory, single instance, no overbooking.")
                .version("v1"));
    }
}
