package com.giojo.flightdata;

import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "flight")
@Getter
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String airlineName;

    @Column(nullable = false, length = 60)
    private String supplierName;

    @Column(nullable = false)
    private long ticketFareCents;

    @Column(nullable = false, length = 3)
    private String departureAirportCode;

    @Column(nullable = false, length = 3)
    private String destinationAirportCode;

    @Column(nullable = false)
    private Instant departureTimeUtc;

    @Column(nullable = false)
    private Instant arrivalTimeUtc;

    protected Flight() {
    }

    public static Flight create(String airlineName,
            String supplierName,
            long ticketFareCents,
            String departureAirportCode,
            String destinationAirportCode,
            Instant departureTimeUtc,
            Instant arrivalTimeUtc) {

        long fare = Objects.requireNonNull(ticketFareCents, "ticketFareCents");
        Instant departureTime = Objects.requireNonNull(departureTimeUtc, "departureTimeUtc");
        Instant arrivalTime = Objects.requireNonNull(arrivalTimeUtc, "arrivalTimeUtc");
        String dep = Objects.requireNonNull(departureAirportCode, "departureAirportCode").toUpperCase();
        String dest = Objects.requireNonNull(destinationAirportCode, "destinationAirportCode").toUpperCase();

        if (dep.equals(dest)) {
            throw new IllegalArgumentException("Destination airport and departure airport can't be the same.");
        }
        if (!arrivalTime.isAfter(departureTime)) {
            throw new IllegalArgumentException("Arrival time must be after departure time.");
        }
        if (fare < 0) {
            throw new IllegalArgumentException("Ticket fare can't be negative.");
        }

        var flight = new Flight();
        flight.airlineName = airlineName;
        flight.supplierName = supplierName;
        flight.ticketFareCents = fare;
        flight.departureAirportCode = dep;
        flight.destinationAirportCode = dest;
        flight.departureTimeUtc = departureTime;
        flight.arrivalTimeUtc = arrivalTime;

        return flight;
    }

}
