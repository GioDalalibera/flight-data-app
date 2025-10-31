package com.giojo.flightdata.flight;

import org.springframework.data.jpa.domain.Specification;

import com.giojo.flightdata.common.utils.DateTimeUtils;
import com.giojo.flightdata.flight.dto.FlightFilter;

import jakarta.persistence.criteria.Predicate;

public final class FlightSpecifications {

    private FlightSpecifications() {
    }

    public static Specification<Flight> fromFilter(FlightFilter f) {
        return (root, q, cb) -> {
            var predicates = new java.util.ArrayList<Predicate>();

            if (f == null)
                return cb.conjunction();

            if (isPresent(f.airline())) {
                predicates.add(cb.like(cb.lower(root.get("airlineName")),
                        "%" + f.airline().trim().toLowerCase() + "%"));
            }
            if (isPresent(f.departure())) {
                predicates.add(cb.equal(root.get("departureAirportCode"),
                        f.departure().trim().toUpperCase()));
            }
            if (isPresent(f.destination())) {
                predicates.add(cb.equal(root.get("destinationAirportCode"),
                        f.destination().trim().toUpperCase()));
            }
            if (f.departFromUtc() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("departureTimeUtc"),
                        DateTimeUtils.startOfDayUtc(f.departFromUtc())));
            }
            if (f.departToUtc() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("departureTimeUtc"), DateTimeUtils.endOfDayUtc(f.departToUtc())));
            }
            if (f.arriveFromUtc() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("arrivalTimeUtc"),
                        DateTimeUtils.startOfDayUtc(f.arriveFromUtc())));
            }
            if (f.arriveToUtc() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("arrivalTimeUtc"), DateTimeUtils.endOfDayUtc(f.arriveToUtc())));
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static boolean isPresent(String s) {
        return s != null && !s.isBlank();
    }
}