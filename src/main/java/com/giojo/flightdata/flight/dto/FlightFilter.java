package com.giojo.flightdata.flight.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.constraints.Pattern;

public record FlightFilter(
        @RequestParam(required = false) String airline,
        @RequestParam(required = false) @Pattern(regexp = "^[A-Za-z]{3}$") String departure,
        @RequestParam(required = false) @Pattern(regexp = "^[A-Za-z]{3}$") String destination,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departFromUtc,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departToUtc,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate arriveFromUtc,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate arriveToUtc) {
}
