package com.giojo.flightdata.flight;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.giojo.flightdata.flight.dto.FlightResponse;
import com.giojo.flightdata.flight.dto.FlightWriteRequest;

@Mapper(componentModel = "spring")
public interface FlightMapper {
    default Flight toEntity(FlightWriteRequest request) {
        return Flight.create(
                request.airlineName(),
                request.supplierName(),
                request.ticketFareCents(),
                request.departureAirportCode(),
                request.destinationAirportCode(),
                request.departureTimeUtc(),
                request.arrivalTimeUtc());
    }

    FlightResponse toResponse(Flight entity);

    default void update(@MappingTarget Flight entity, FlightWriteRequest request) {
        entity.applyUpdate(
                request.airlineName(),
                request.supplierName(),
                request.ticketFareCents(),
                request.departureAirportCode(),
                request.destinationAirportCode(),
                request.departureTimeUtc(),
                request.arrivalTimeUtc());
    }
}