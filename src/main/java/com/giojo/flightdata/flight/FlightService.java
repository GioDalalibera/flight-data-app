package com.giojo.flightdata.flight;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.giojo.flightdata.common.exceptions.BadRequestException;
import com.giojo.flightdata.common.exceptions.ResourceNotFoundException;
import com.giojo.flightdata.flight.dto.FlightResponse;
import com.giojo.flightdata.flight.dto.FlightWriteRequest;

@Service
@Transactional(readOnly = true)
public class FlightService {

    static final int MAX_PAGE_SIZE = 300;

    private final FlightRepository repository;

    private final FlightMapper mapper;

    public FlightService(FlightRepository repository, FlightMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public FlightResponse getFlight(long id) {
        return mapper.toResponse(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight %d not found".formatted(id))));
    }

    public Page<FlightResponse> fetchFlights(Pageable pageable) {
        if (pageable.getPageSize() > MAX_PAGE_SIZE) {
            throw new BadRequestException("page_size can't exceed %d".formatted(MAX_PAGE_SIZE));
        }

        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional
    public FlightResponse createFlight(FlightWriteRequest flight) {
        return mapper.toResponse(repository.save(mapper.toEntity(flight)));
    }

    @Transactional
    public FlightResponse updateFlight(long id, FlightWriteRequest flightRequest) {
        Flight flight = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight %d not found".formatted(id)));
        mapper.update(flight, flightRequest);
        return mapper.toResponse(flight);
    }

    @Transactional
    public void deleteFlight(long id) {
        repository.findById(id).ifPresent(repository::delete);
    }
}