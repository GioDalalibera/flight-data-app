package com.giojo.flightdata.flight;

import org.springframework.data.jpa.domain.Specification;

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
                predicates.add(cb.greaterThanOrEqualTo(root.get("departureTimeUtc"), f.departFromUtc()));
            }
            if (f.departToUtc() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("departureTimeUtc"), f.departToUtc()));
            }
            if (f.arriveFromUtc() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("arrivalTimeUtc"), f.arriveFromUtc()));
            }
            if (f.arriveToUtc() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("arrivalTimeUtc"), f.arriveToUtc()));
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static boolean isPresent(String s) {
        return s != null && !s.isBlank();
    }
}