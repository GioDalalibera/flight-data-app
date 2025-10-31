package com.giojo.flightdata.flight.mappers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.giojo.flightdata.flight.Flight;
import com.giojo.flightdata.flight.dto.FlightResponse;
import com.giojo.flightdata.flight.dto.FlightWriteRequest;

public class FlightMapperTest {

        FlightMapper flightMapper;

        @BeforeEach
        public void setup() {
                flightMapper = new FlightMapperImpl();
        }

        @Test
        public void toEntityTest() {
                FlightWriteRequest flightRequest = new FlightWriteRequest("Azul",
                                "CrazySupplier",
                                80,
                                "CWB",
                                "GRU",
                                Instant.parse("2025-10-10T19:00:00Z"),
                                Instant.parse("2025-10-10T21:00:00Z"));

                Flight mappedEntity = flightMapper.toEntity(flightRequest);

                assertAll(
                                () -> assertEquals(flightRequest.airlineName(), mappedEntity.getAirlineName()),
                                () -> assertEquals(flightRequest.supplierName(), mappedEntity.getSupplierName()),
                                () -> assertEquals(flightRequest.ticketFareCents(), mappedEntity.getTicketFareCents()),
                                () -> assertEquals(flightRequest.departureAirportCode(),
                                                mappedEntity.getDepartureAirportCode()),
                                () -> assertEquals(flightRequest.destinationAirportCode(),
                                                mappedEntity.getDestinationAirportCode()),
                                () -> assertEquals(flightRequest.departureTimeUtc(),
                                                mappedEntity.getDepartureTimeUtc()),
                                () -> assertEquals(flightRequest.arrivalTimeUtc(), mappedEntity.getArrivalTimeUtc()));
        }

        @Test
        public void toResponseTest() {
                Flight flight = Flight.create("Delta",
                                "FlightDataApp",
                                120,
                                "GRU",
                                "POA",
                                Instant.parse("2025-10-29T19:00:00Z"),
                                Instant.parse("2025-10-29T21:00:00Z"));

                FlightResponse mappedResponse = flightMapper.toResponse(flight);

                assertAll(
                                () -> assertEquals(flight.getAirlineName(), mappedResponse.airlineName()),
                                () -> assertEquals(flight.getSupplierName(), mappedResponse.supplierName()),
                                () -> assertEquals(flight.getTicketFareCents(), mappedResponse.ticketFareCents()),
                                () -> assertEquals(flight.getDepartureAirportCode(),
                                                mappedResponse.departureAirportCode()),
                                () -> assertEquals(flight.getDestinationAirportCode(),
                                                mappedResponse.destinationAirportCode()),
                                () -> assertEquals(flight.getDepartureTimeUtc(), mappedResponse.departureTimeUtc()),
                                () -> assertEquals(flight.getArrivalTimeUtc(), mappedResponse.arrivalTimeUtc()));
        }

        @Test
        public void udpateTest() {
                FlightWriteRequest flightRequest = new FlightWriteRequest("Azul",
                                "CrazySupplier",
                                80,
                                "CWB",
                                "GRU",
                                Instant.parse("2025-10-10T19:00:00Z"),
                                Instant.parse("2025-10-10T21:00:00Z"));

                Flight flight = Flight.create("Delta",
                                "FlightDataApp",
                                120,
                                "GRU",
                                "POA",
                                Instant.parse("2025-10-29T19:00:00Z"),
                                Instant.parse("2025-10-29T21:00:00Z"));

                flightMapper.update(flight, flightRequest);

                assertAll(
                                () -> assertEquals(flight.getAirlineName(), flightRequest.airlineName()),
                                () -> assertEquals(flight.getSupplierName(), flightRequest.supplierName()),
                                () -> assertEquals(flight.getTicketFareCents(), flightRequest.ticketFareCents()),
                                () -> assertEquals(flight.getDepartureAirportCode(),
                                                flightRequest.departureAirportCode()),
                                () -> assertEquals(flight.getDestinationAirportCode(),
                                                flightRequest.destinationAirportCode()),
                                () -> assertEquals(flight.getDepartureTimeUtc(), flightRequest.departureTimeUtc()),
                                () -> assertEquals(flight.getArrivalTimeUtc(), flightRequest.arrivalTimeUtc()));
        }
}
