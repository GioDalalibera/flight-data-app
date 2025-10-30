package com.giojo.flightdata.flight.dto;

import static java.time.format.DateTimeFormatter.ISO_INSTANT;

import java.time.Instant;

public record FlightResponse(
        long id,
        String airlineName,
        String supplierName,
        long ticketFareCents,
        String departureAirportCode,
        String destinationAirportCode,
        Instant departureTimeUtc,
        Instant arrivalTimeUtc) {

    public final String toLogString() {
        return String.format("#%s %s %s -> %s @%s", id, airlineName, departureAirportCode, destinationAirportCode,
                departureTimeUtc != null ? ISO_INSTANT.format(departureTimeUtc) : "?");
    }
}
