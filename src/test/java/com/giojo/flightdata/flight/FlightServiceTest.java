package com.giojo.flightdata.flight;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import com.giojo.flightdata.common.exceptions.BadRequestException;
import com.giojo.flightdata.common.exceptions.ResourceNotFoundException;
import com.giojo.flightdata.flight.dto.FlightResponse;
import com.giojo.flightdata.flight.dto.FlightWriteRequest;
import com.giojo.flightdata.flight.mappers.FlightMapper;

@ExtendWith(MockitoExtension.class)
public class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private FlightMapper flightMapper;

    @InjectMocks
    private FlightService flightService;

    @Test
    public void getFlightTestNotFound() {
        Long flightId = 1L;
        when(flightRepository.findById(eq(flightId))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> flightService.getFlight(flightId));

        verifyNoInteractions(flightMapper);
        verifyNoMoreInteractions(flightRepository);
    }

    @Test
    public void getFlightTest() {
        Long flightId = 1L;
        Flight flight = Flight.create("Delta",
                "FlightDataApp",
                120,
                "GRU",
                "POA",
                Instant.parse("2025-10-29T19:00:00Z"),
                Instant.parse("2025-10-29T21:00:00Z"));

        FlightResponse mappedResponse = getResponseFromFlight(flight);

        when(flightRepository.findById(eq(flightId))).thenReturn(Optional.of(flight));
        when(flightMapper.toResponse(flight)).thenReturn(mappedResponse);

        FlightResponse result = flightService.getFlight(flightId);

        Assertions.assertAll(
                () -> assertEquals(flight.getAirlineName(), result.airlineName()),
                () -> assertEquals(flight.getSupplierName(), result.supplierName()),
                () -> assertEquals(flight.getTicketFareCents(), result.ticketFareCents()),
                () -> assertEquals(flight.getDepartureAirportCode(), result.departureAirportCode()),
                () -> assertEquals(flight.getDestinationAirportCode(), result.destinationAirportCode()),
                () -> assertEquals(flight.getDepartureTimeUtc(), result.departureTimeUtc()),
                () -> assertEquals(flight.getArrivalTimeUtc(), result.arrivalTimeUtc()));

        verify(flightMapper, times(1)).toResponse(flight);
        verify(flightRepository, times(1)).findById(eq(flightId));
        verifyNoMoreInteractions(flightRepository, flightMapper);
    }

    @Test
    public void fetchFlightsTestPageSizeTooBig() {
        Pageable pageable = Pageable.ofSize(500);

        assertThrows(BadRequestException.class, () -> flightService.fetchFlights(null, pageable));
        verifyNoInteractions(flightRepository, flightMapper);
    }

    @Test
    public void createFlightTest() {
        FlightWriteRequest flightRequest = new FlightWriteRequest("Azul",
                "CrazySupplier",
                80,
                "CWB",
                "POA",
                Instant.parse("2025-10-29T20:00:00Z"),
                Instant.parse("2025-10-29T20:50:00Z"));

        Flight flight = getFlightFromFlightRequest(flightRequest);
        FlightResponse mappedResponse = getResponseFromFlight(flight);

        when(flightMapper.toEntity(flightRequest)).thenReturn(flight);
        when(flightMapper.toResponse(flight)).thenReturn(mappedResponse);
        when(flightRepository.save(flight)).thenReturn(flight);

        FlightResponse savedResult = flightService.createFlight(flightRequest);

        assertAll(
                () -> assertEquals(flightRequest.airlineName(), savedResult.airlineName()),
                () -> assertEquals(flightRequest.supplierName(), savedResult.supplierName()),
                () -> assertEquals(flightRequest.ticketFareCents(), savedResult.ticketFareCents()),
                () -> assertEquals(flightRequest.departureAirportCode(), savedResult.departureAirportCode()),
                () -> assertEquals(flightRequest.destinationAirportCode(), savedResult.destinationAirportCode()),
                () -> assertEquals(flightRequest.departureTimeUtc(), savedResult.departureTimeUtc()),
                () -> assertEquals(flightRequest.arrivalTimeUtc(), savedResult.arrivalTimeUtc()));

        verify(flightMapper, times(1)).toEntity(flightRequest);
        verify(flightMapper, times(1)).toResponse(flight);
        verify(flightRepository, times(1)).save(flight);
        verifyNoMoreInteractions(flightRepository, flightMapper);
    }

    @Test
    public void updateFlightNotFoundTest() {
        Long flightId = 1L;
        FlightWriteRequest flightRequest = new FlightWriteRequest("TAM",
                "FlightDataApp",
                230,
                "GRU",
                "LIS",
                Instant.parse("2025-10-29T20:00:00Z"),
                Instant.parse("2025-10-30T14:35:00Z"));

        when(flightRepository.findById(eq(flightId))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> flightService.updateFlight(flightId, flightRequest));
        verify(flightRepository, times(1)).findById(eq(flightId));
        verifyNoInteractions(flightMapper);
        verifyNoMoreInteractions(flightRepository);
    }

    @Test
    public void updateFlightTest() {
        Long flightId = 1L;
        Flight flight = Flight.create("Delta",
                "FlightDataApp",
                120,
                "GRU",
                "POA",
                Instant.parse("2025-10-29T19:00:00Z"),
                Instant.parse("2025-10-29T21:00:00Z"));

        FlightWriteRequest flightRequest = new FlightWriteRequest("Azul",
                "CrazySupplier",
                80,
                "CWB",
                "POA",
                Instant.parse("2025-10-29T20:00:00Z"),
                Instant.parse("2025-10-29T20:50:00Z"));

        FlightResponse mappedResponse = getResponseFromFlight(flight);

        when(flightMapper.toResponse(flight)).thenReturn(mappedResponse);
        when(flightRepository.findById(eq(flightId))).thenReturn(Optional.of(flight));

        flightService.updateFlight(flightId, flightRequest);

        verify(flightMapper, times(1)).update(flight, flightRequest);
        verify(flightMapper, times(1)).toResponse(flight);
        verify(flightRepository, times(1)).findById(eq(flightId));
        verifyNoMoreInteractions(flightRepository, flightMapper);
    }

    @Test
    public void deleteFlightTest() {
        Long flightId = 1L;
        Flight flight = Flight.create(
                "Airline",
                "Supplier",
                100,
                "GRU",
                "POA",
                Instant.parse("2025-10-29T20:00:00Z"),
                Instant.parse("2025-10-29T20:50:00Z"));

        when(flightRepository.findById(eq(flightId))).thenReturn(Optional.of(flight));

        flightService.deleteFlight(flightId);

        verify(flightRepository, times(1)).findById(eq(flightId));
        verify(flightRepository, times(1)).delete(eq(flight));
        verifyNoInteractions(flightMapper);
        verifyNoMoreInteractions(flightRepository);
    }

    private FlightResponse getResponseFromFlight(Flight flight) {
        return new FlightResponse(flight.getId(),
                flight.getAirlineName(),
                flight.getSupplierName(),
                flight.getTicketFareCents(),
                flight.getDepartureAirportCode(),
                flight.getDestinationAirportCode(),
                flight.getDepartureTimeUtc(),
                flight.getArrivalTimeUtc());
    }

    private Flight getFlightFromFlightRequest(FlightWriteRequest flightRequest) {
        return Flight.create(flightRequest.airlineName(),
                flightRequest.supplierName(),
                flightRequest.ticketFareCents(),
                flightRequest.departureAirportCode(),
                flightRequest.destinationAirportCode(),
                flightRequest.departureTimeUtc(),
                flightRequest.arrivalTimeUtc());
    }

}
