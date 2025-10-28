package com.giojo.flightdata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;

import org.junit.jupiter.api.Test;

public class FlightTest {

        private static final String AIRLINE_NAME = "Test Airlines";
        private static final String SUPPLIER_NAME = "Test Supplier";
        private static final long TICKET_FARE = 120;
        private static final String DEPARTURE_AIRPORT_CODE = "LIS";
        private static final String DESTINATION_AIRPORT_CODE = "GRU";
        private static final Instant DEPARTURE_TIME_UTC = Instant.parse("2025-10-28T22:00:00Z");
        private static final Instant ARRIVAL_TIME_UTC = Instant.parse("2025-10-29T16:30:00Z");

        @Test
        public void rejectSameAirportCodes() {
                assertThrows(
                                IllegalArgumentException.class,
                                () -> Flight.create(AIRLINE_NAME, SUPPLIER_NAME, TICKET_FARE, DEPARTURE_AIRPORT_CODE,
                                                DEPARTURE_AIRPORT_CODE, DEPARTURE_TIME_UTC, ARRIVAL_TIME_UTC));
        }

        @Test
        public void rejectNegativeTicketFare() {
                assertThrows(
                                IllegalArgumentException.class,
                                () -> Flight.create(AIRLINE_NAME, SUPPLIER_NAME, -100, DEPARTURE_AIRPORT_CODE,
                                                DESTINATION_AIRPORT_CODE,
                                                DEPARTURE_TIME_UTC, ARRIVAL_TIME_UTC));
        }

        @Test
        public void rejectDepartureAfterArrival() {
                assertThrows(
                                IllegalArgumentException.class,
                                () -> Flight.create(AIRLINE_NAME, SUPPLIER_NAME, TICKET_FARE, DEPARTURE_AIRPORT_CODE,
                                                DESTINATION_AIRPORT_CODE,
                                                ARRIVAL_TIME_UTC, DEPARTURE_TIME_UTC));
        }

        @Test
        public void rejectDepartureTimeEqualsArrivalTime() {
                assertThrows(
                                IllegalArgumentException.class,
                                () -> Flight.create(AIRLINE_NAME, SUPPLIER_NAME, TICKET_FARE, DEPARTURE_AIRPORT_CODE,
                                                DESTINATION_AIRPORT_CODE,
                                                ARRIVAL_TIME_UTC, ARRIVAL_TIME_UTC));
        }

        @Test
        public void createValidFlight() {
                var flight = Flight.create(AIRLINE_NAME, SUPPLIER_NAME, TICKET_FARE, DEPARTURE_AIRPORT_CODE,
                                DESTINATION_AIRPORT_CODE, DEPARTURE_TIME_UTC, ARRIVAL_TIME_UTC);

                assertEquals(AIRLINE_NAME, flight.getAirlineName());
                assertEquals(SUPPLIER_NAME, flight.getSupplierName());
                assertEquals(TICKET_FARE, flight.getTicketFareCents());
                assertEquals(DEPARTURE_AIRPORT_CODE, flight.getDepartureAirportCode());
                assertEquals(DESTINATION_AIRPORT_CODE, flight.getDestinationAirportCode());
                assertEquals(DEPARTURE_TIME_UTC, flight.getDepartureTimeUtc());
                assertEquals(ARRIVAL_TIME_UTC, flight.getArrivalTimeUtc());
        }

}
