package com.giojo.flightdata.flight;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.giojo.flightdata.common.exceptions.ResourceNotFoundException;
import com.giojo.flightdata.flight.dto.FlightResponse;
import com.giojo.flightdata.flight.dto.FlightWriteRequest;

@WebMvcTest(FlightController.class)
class FlightControllerTest {

  @Autowired
  MockMvc mvc;

  @Autowired
  ObjectMapper om;

  @MockitoBean
  FlightService flightService;

  @Test
  public void getPagedFlights() throws Exception {
    long flightId = 1L;
    FlightResponse response = new FlightResponse(
        flightId,
        "Azul",
        "CrazySupplier",
        80,
        "CWB",
        "GRU",
        Instant.parse("2025-10-10T19:00:00Z"),
        Instant.parse("2025-10-10T21:00:00Z"));

    int pageNumber = 0;
    int pageSize = 20;
    int totalElements = 1;

    Page<FlightResponse> page = new PageImpl<>(
        List.of(response), PageRequest.of(pageNumber, pageSize, Sort.by("departureTimeUtc")), totalElements);

    when(flightService.fetchFlights(any(Pageable.class))).thenReturn(page);

    mvc.perform(get("/api/v1/flights")
        .param("page", Integer.toString(pageNumber)).param("size", Integer.toString(pageSize))
        .param("sort", "departureTimeUtc,asc"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content[0].id").value(flightId))
        .andExpect(jsonPath("$.size").value(pageSize))
        .andExpect(jsonPath("$.number").value(pageNumber))
        .andExpect(jsonPath("$.totalElements").value(totalElements));
  }

  @Test
  public void getFlight() throws Exception {
    long flightId = 7L;
    FlightResponse response = new FlightResponse(
        flightId,
        "Azul",
        "CrazySupplier",
        80,
        "CWB",
        "GRU",
        Instant.parse("2025-10-10T19:00:00Z"),
        Instant.parse("2025-10-10T21:00:00Z"));

    when(flightService.getFlight(eq(flightId))).thenReturn(response);

    mvc.perform(get("/api/v1/flights/" + flightId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(flightId))
        .andExpect(jsonPath("$.airlineName").value("Azul"))
        .andExpect(jsonPath("$.supplierName").value("CrazySupplier"))
        .andExpect(jsonPath("$.departureAirportCode").value("CWB"))
        .andExpect(jsonPath("$.destinationAirportCode").value("GRU"));
  }

  @Test
  public void getFlightNotFound() throws Exception {
    long flightId = 7L;
    when(flightService.getFlight(eq(flightId))).thenThrow(ResourceNotFoundException.class);

    mvc.perform(get("/api/v1/flights/" + flightId))
        .andExpect(status().isNotFound());
  }

  @Test
  public void createFlight() throws Exception {
    FlightWriteRequest request = new FlightWriteRequest(
        "Delta",
        "FlightDataApp",
        79,
        "GRU",
        "LIS",
        Instant.parse("2025-10-10T19:00:00Z"),
        Instant.parse("2025-10-11T21:00:00Z"));

    Long createdId = 42L;
    FlightResponse response = new FlightResponse(createdId,
        request.airlineName(),
        request.supplierName(),
        request.ticketFareCents(),
        request.departureAirportCode(),
        request.destinationAirportCode(),
        request.departureTimeUtc(),
        request.arrivalTimeUtc());

    when(flightService.createFlight(any())).thenReturn(response);

    mvc.perform(post("/api/v1/flights")
        .contentType(MediaType.APPLICATION_JSON)
        .content(om.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(createdId));
  }

  @Test
  public void updateFlight() throws Exception {
    FlightWriteRequest request = new FlightWriteRequest(
        "Delta",
        "FlightDataApp",
        79,
        "GRU",
        "LIS",
        Instant.parse("2025-10-10T19:00:00Z"),
        Instant.parse("2025-10-11T21:00:00Z"));

    Long flightId = 42L;
    FlightResponse response = getResponseFromFlightWriteRequest(flightId, request);

    when(flightService.updateFlight(eq(flightId), eq(request))).thenReturn(response);

    mvc.perform(put("/api/v1/flights/42")
        .contentType(MediaType.APPLICATION_JSON)
        .content(om.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.airlineName").value(request.airlineName()))
        .andExpect(jsonPath("$.supplierName").value(request.supplierName()))
        .andExpect(jsonPath("$.departureAirportCode").value(request.departureAirportCode()))
        .andExpect(jsonPath("$.destinationAirportCode").value(request.destinationAirportCode()))
        .andExpect(jsonPath("$.ticketFareCents").value(request.ticketFareCents()));
  }

  @Test
  public void updateFlightNotFound() throws Exception {
    FlightWriteRequest request = new FlightWriteRequest(
        "Delta",
        "FlightDataApp",
        79,
        "GRU",
        "LIS",
        Instant.parse("2025-10-10T19:00:00Z"),
        Instant.parse("2025-10-11T21:00:00Z"));

    Long flightId = 42L;

    when(flightService.updateFlight(eq(flightId), eq(request))).thenThrow(ResourceNotFoundException.class);

    mvc.perform(put("/api/v1/flights/42")
        .contentType(MediaType.APPLICATION_JSON)
        .content(om.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  public void deleteFlight() throws Exception {
    Long flightId = 10L;
    mvc.perform(delete("/api/v1/flights/" + flightId))
        .andExpect(status().isNoContent());
    verify(flightService).deleteFlight(flightId);
  }

  @Test
  public void createFlightFailValidationBlankNames() throws Exception {
    String badJson = """
        {"airlineName":"", "supplierName":"", "ticketFareCents": 150,
         "departureAirportCode":"CWB", "destinationAirportCode":"GRU",
         "departureTimeUtc":"2025-10-10T12:00:00Z", "arrivalTimeUtc":"2025-10-10T19:00:00Z"}
        """;

    mvc.perform(post("/api/v1/flights")
        .contentType(MediaType.APPLICATION_JSON)
        .content(badJson))
        .andExpect(status().isBadRequest());

    verifyNoInteractions(flightService);
  }

  @Test
  public void createFlightFailValidationNegativeFare() throws Exception {
    String badJson = """
        {"airlineName":"Azul", "supplierName":"FlightDataApp", "ticketFareCents": -80,
         "departureAirportCode":"CWB", "destinationAirportCode":"GRU",
         "departureTimeUtc":"2025-10-10T12:00:00Z", "arrivalTimeUtc":"2025-10-10T19:00:00Z"}
        """;

    mvc.perform(post("/api/v1/flights")
        .contentType(MediaType.APPLICATION_JSON)
        .content(badJson))
        .andExpect(status().isBadRequest());

    verifyNoInteractions(flightService);
  }

  @Test
  public void createFlightFailValidationNullTimes() throws Exception {
    String badJson = """
        {"airlineName":"Azul", "supplierName":"FlightDataApp", "ticketFareCents": 80,
         "departureAirportCode":"CWB", "destinationAirportCode":"GRU",
         "departureTimeUtc":null, "arrivalTimeUtc":null}
        """;

    mvc.perform(post("/api/v1/flights")
        .contentType(MediaType.APPLICATION_JSON)
        .content(badJson))
        .andExpect(status().isBadRequest());

    verifyNoInteractions(flightService);
  }

  @Test
  public void createFlightFailValidationNullAirportCodes() throws Exception {
    String badJson = """
        {"airlineName":"Azul", "supplierName":"FlightDataApp", "ticketFareCents": -80,
         "departureAirportCode":null, "destinationAirportCode":null,
         "departureTimeUtc":"2025-10-10T12:00:00Z", "arrivalTimeUtc":"2025-10-10T19:00:00Z"}
        """;

    mvc.perform(post("/api/v1/flights")
        .contentType(MediaType.APPLICATION_JSON)
        .content(badJson))
        .andExpect(status().isBadRequest());

    verifyNoInteractions(flightService);
  }

  private FlightResponse getResponseFromFlightWriteRequest(Long id, FlightWriteRequest flightRequest) {
    return new FlightResponse(id,
        flightRequest.airlineName(),
        flightRequest.supplierName(),
        flightRequest.ticketFareCents(),
        flightRequest.departureAirportCode(),
        flightRequest.destinationAirportCode(),
        flightRequest.departureTimeUtc(),
        flightRequest.arrivalTimeUtc());
  }
}