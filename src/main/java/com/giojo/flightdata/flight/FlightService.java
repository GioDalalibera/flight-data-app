package com.giojo.flightdata.flight;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.giojo.flightdata.common.exceptions.BadRequestException;
import com.giojo.flightdata.common.exceptions.ResourceNotFoundException;
import com.giojo.flightdata.flight.dto.FlightFilter;
import com.giojo.flightdata.flight.dto.FlightResponse;
import com.giojo.flightdata.flight.dto.FlightWriteRequest;
import com.giojo.flightdata.flight.mappers.CrazySupplierMapper;
import com.giojo.flightdata.flight.mappers.FlightMapper;

@Service
@Transactional(readOnly = true)
public class FlightService {

    static final int MAX_PAGE_SIZE = 300;

    private final FlightRepository repository;

    private final FlightMapper mapper;

    private final CrazySupplierClient crazySupplierClient;

    public FlightService(FlightRepository repository, FlightMapper mapper, CrazySupplierClient crazySupplierClient) {
        this.repository = repository;
        this.mapper = mapper;
        this.crazySupplierClient = crazySupplierClient;
    }

    public FlightResponse getFlight(long id) {
        return mapper.toResponse(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight %d not found".formatted(id))));
    }

    public Page<FlightResponse> fetchFlights(FlightFilter filter, Pageable pageable) {
        validateParameters(filter, pageable);

        List<FlightResponse> dbPage = repository
                .findAll(FlightSpecifications.fromFilter(filter), pageable.getSort())
                .stream()
                .map(mapper::toResponse)
                .toList();

        List<FlightResponse> supplierRows = crazySupplierClient
                .search(CrazySupplierMapper.fromFlightFilter(filter))
                .stream()
                .map(CrazySupplierMapper::toFlightResponse)
                .toList();

        List<FlightResponse> merged = getMergedResults(dbPage, supplierRows);
        sortMerged(pageable, merged);
        return paginate(pageable, merged);
    }

    private Page<FlightResponse> paginate(Pageable pageable, List<FlightResponse> merged) {
        int from = pageable.getPageNumber() * pageable.getPageSize();
        if (from >= merged.size()) {
            return new PageImpl<>(List.of(), pageable, merged.size());
        }
        int to = Math.min(from + pageable.getPageSize(), merged.size());
        return new PageImpl<>(merged.subList(from, to), pageable, merged.size());

    }

    private List<FlightResponse> getMergedResults(List<FlightResponse> dbRows, List<FlightResponse> supplierRows) {
        List<FlightResponse> merged = new ArrayList<>(dbRows.size() + supplierRows.size());
        merged.addAll(dbRows);
        merged.addAll(supplierRows);
        return merged;
    }

    private void sortMerged(Pageable pageable, List<FlightResponse> merged) {
        Comparator<FlightResponse> comparator = sortComparator(pageable.getSort());
        if (comparator != null) {
            merged.sort(comparator);
        }
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

    private static void validateParameters(FlightFilter filter, Pageable pageable) {
        if (pageable.getPageSize() > MAX_PAGE_SIZE) {
            throw new BadRequestException("page_size can't exceed %d".formatted(MAX_PAGE_SIZE));
        }

        if (filter == null)
            return;
        if (filter.departFromUtc() != null && filter.departToUtc() != null &&
                filter.departFromUtc().isAfter(filter.departToUtc())) {
            throw new BadRequestException("departToUtc must be after departFromUtc");
        }
        if (filter.arriveFromUtc() != null && filter.arriveToUtc() != null &&
                filter.arriveFromUtc().isAfter(filter.arriveToUtc())) {
            throw new BadRequestException("arriveToUtc must be after arriveFromUtc");
        }
        if (filter.arriveToUtc() != null && filter.departFromUtc() != null &&
                filter.departFromUtc().isAfter(filter.arriveToUtc())) {
            throw new BadRequestException("arriveToUtc must be after departFromUtc");
        }
    }

    private static Comparator<FlightResponse> sortComparator(Sort sort) {
        if (sort == null || sort.isUnsorted())
            return null;

        Comparator<FlightResponse> c = null;
        for (Sort.Order o : sort) {
            Comparator<FlightResponse> next = comparatorFor(o.getProperty(), o.isAscending());
            c = (c == null) ? next : c.thenComparing(next);
        }
        return c;
    }

    private static Comparator<FlightResponse> comparatorFor(String prop, boolean asc) {
        Comparator<FlightResponse> c;
        switch (prop) {
            case "airlineName" -> c = Comparator.comparing(FlightResponse::airlineName,
                    Comparator.nullsLast(String::compareToIgnoreCase));
            case "supplierName" -> c = Comparator.comparing(FlightResponse::supplierName,
                    Comparator.nullsLast(String::compareToIgnoreCase));
            case "ticketFareCents" -> c = Comparator.comparingLong(FlightResponse::ticketFareCents);
            case "departureAirportCode" -> c = Comparator.comparing(FlightResponse::departureAirportCode,
                    Comparator.nullsLast(String::compareTo));
            case "destinationAirportCode" -> c = Comparator.comparing(FlightResponse::destinationAirportCode,
                    Comparator.nullsLast(String::compareTo));
            case "departureTimeUtc" -> c = Comparator.comparing(FlightResponse::departureTimeUtc,
                    Comparator.nullsLast(Instant::compareTo));
            case "arrivalTimeUtc" -> c = Comparator.comparing(FlightResponse::arrivalTimeUtc,
                    Comparator.nullsLast(Instant::compareTo));
            default -> c = Comparator.comparing(FlightResponse::id, Comparator.nullsLast(Long::compareTo));
        }
        return asc ? c : c.reversed();
    }
}