package com.giojo.flightdata.flight;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import com.giojo.flightdata.flight.dto.FlightResponse;
import com.giojo.flightdata.flight.dto.FlightWriteRequest;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping
    public Page<FlightResponse> getFlights(Pageable pageable) {
        return flightService.fetchFlights(pageable);
    }

    @GetMapping("/{id}")
    public FlightResponse getFlight(@PathVariable Long id) {
        return flightService.getFlight(id);
    }

    @PostMapping
    public ResponseEntity<FlightResponse> createFlight(@Valid @RequestBody FlightWriteRequest flightWriteRequest) {
        FlightResponse result = flightService.createFlight(flightWriteRequest);
        URI location = URI.create("/api/v1/flights/" + result.id());
        return ResponseEntity.created(location).body(result);
    }

    @PutMapping("/{id}")
    public FlightResponse updateFlight(@PathVariable Long id,
            @Valid @RequestBody FlightWriteRequest flightWriteRequest) {
        return flightService.updateFlight(id, flightWriteRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
        return ResponseEntity.noContent().build();
    }

}
