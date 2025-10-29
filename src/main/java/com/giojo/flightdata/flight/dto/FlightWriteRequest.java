package com.giojo.flightdata.flight.dto;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record FlightWriteRequest(
                @NotBlank @Size(max = 60) String airlineName,
                @NotBlank @Size(max = 60) String supplierName,
                @PositiveOrZero long ticketFareCents,
                @NotBlank @Pattern(regexp = "[A-Za-z]{3}") String departureAirportCode,
                @NotBlank @Pattern(regexp = "[A-Za-z]{3}") String destinationAirportCode,
                @NotNull Instant departureTimeUtc,
                @NotNull Instant arrivalTimeUtc) {
}