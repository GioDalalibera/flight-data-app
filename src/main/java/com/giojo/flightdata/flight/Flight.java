package com.giojo.flightdata.flight;

import static java.time.format.DateTimeFormatter.ISO_INSTANT;

import java.time.Instant;
import java.util.Objects;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

@Entity
@Table(name = "flight", indexes = {
        @Index(name = "idx_flight_dep_code", columnList = "departureAirportCode"),
        @Index(name = "idx_flight_dest_code", columnList = "destinationAirportCode"),
        @Index(name = "idx_flight_departure_time", columnList = "departureTimeUtc"),
        @Index(name = "idx_flight_arrival_time", columnList = "arrivalTimeUtc"),
        @Index(name = "idx_flight_airline_name", columnList = "airlineName") })
@Getter
@ToString
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

    @Column(nullable = false, length = 3, columnDefinition = "char(3)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private String departureAirportCode;

    @Column(nullable = false, length = 3, columnDefinition = "char(3)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private String destinationAirportCode;

    @Column(nullable = false)
    private Instant departureTimeUtc;

    @Column(nullable = false)
    private Instant arrivalTimeUtc;

    @PrePersist
    @PreUpdate
    void normalize() {
        if (airlineName != null) {
            airlineName = airlineName.trim();
        }
        if (supplierName != null) {
            supplierName = supplierName.trim();
        }
        if (departureAirportCode != null) {
            departureAirportCode = departureAirportCode.trim().toUpperCase();
        }
        if (destinationAirportCode != null) {
            destinationAirportCode = destinationAirportCode.trim().toUpperCase();
        }
    }

    protected Flight() {
    }

    public static Flight create(String airlineName,
            String supplierName,
            long ticketFareCents,
            String departureAirportCode,
            String destinationAirportCode,
            Instant departureTimeUtc,
            Instant arrivalTimeUtc) {

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
        if (ticketFareCents < 0) {
            throw new IllegalArgumentException("Ticket fare can't be negative.");
        }

        var flight = new Flight();
        flight.airlineName = airlineName;
        flight.supplierName = supplierName;
        flight.ticketFareCents = ticketFareCents;
        flight.departureAirportCode = dep;
        flight.destinationAirportCode = dest;
        flight.departureTimeUtc = departureTime;
        flight.arrivalTimeUtc = arrivalTime;

        return flight;
    }

    public void applyUpdate(String airlineName,
            String supplierName,
            long ticketFareCents,
            String departureAirportCode,
            String destinationAirportCode,
            Instant departureTimeUtc,
            Instant arrivalTimeUtc) {

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
        if (ticketFareCents < 0) {
            throw new IllegalArgumentException("Ticket fare can't be negative.");
        }

        this.airlineName = airlineName;
        this.supplierName = supplierName;
        this.ticketFareCents = ticketFareCents;
        this.departureAirportCode = dep;
        this.destinationAirportCode = dest;
        this.departureTimeUtc = departureTime;
        this.arrivalTimeUtc = arrivalTime;
    }

    public final String toLogString() {
        return String.format("#%s %s %s -> %s @%s", id, airlineName, departureAirportCode, destinationAirportCode,
                departureTimeUtc != null ? ISO_INSTANT.format(departureTimeUtc) : "?");
    }

}
