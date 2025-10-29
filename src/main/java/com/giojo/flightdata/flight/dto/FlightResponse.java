package com.giojo.flightdata.flight.dto;

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
}
