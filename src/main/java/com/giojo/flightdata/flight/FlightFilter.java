package com.giojo.flightdata.flight;

import java.time.Instant;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.Pattern;

public record FlightFilter(
        String airline,
        @Pattern(regexp = "^[A-Za-z]{3}$") String departure,
        @Pattern(regexp = "^[A-Za-z]{3}$") String destination,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant departFromUtc,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant departToUtc,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant arriveFromUtc,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant arriveToUtc) {
}
