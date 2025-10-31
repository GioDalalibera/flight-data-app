package com.giojo.flightdata.flight.mappers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;

import org.springframework.stereotype.Component;

import com.giojo.flightdata.flight.dto.CrazySupplierRequest;
import com.giojo.flightdata.flight.dto.CrazySupplierResponse;
import com.giojo.flightdata.flight.dto.FlightFilter;
import com.giojo.flightdata.flight.dto.FlightResponse;

@Component
public class CrazySupplierMapper {

    private static final String CRAZY_SUPPLIER_NAME = "CrazySupplier";

    public static FlightResponse toFlightResponse(CrazySupplierResponse responseItem) {
        Instant depUtc = parseDateTimeToUtc(responseItem.outboundDateTime());
        Instant arrUtc = parseDateTimeToUtc(responseItem.inboundDateTime());

        long cents = priceToCents(responseItem.basePrice(), responseItem.tax());

        return new FlightResponse(
                null, // external source, doesn't have an ID
                responseItem.carrier(),
                CRAZY_SUPPLIER_NAME,
                cents,
                responseItem.departureAirportName(),
                responseItem.arrivalAirportName(),
                depUtc,
                arrUtc);
    }

    public static CrazySupplierRequest fromFlightFilter(FlightFilter flightFilter) {
        String departDate = null != flightFilter.departFromUtc() && null == flightFilter.departToUtc()
                ? flightFilter.departFromUtc().toString()
                : null;
        String arriveDate = null != flightFilter.arriveFromUtc() && null == flightFilter.arriveToUtc()
                ? flightFilter.arriveFromUtc().toString()
                : null;

        return new CrazySupplierRequest(
                flightFilter.departure(),
                flightFilter.destination(),
                departDate,
                arriveDate);
    }

    public static Instant parseDateTimeToUtc(String value) {
        if (value == null || value.isBlank())
            return null;
        try {
            return LocalDateTime.parse(value).toInstant(ZoneOffset.UTC);
        } catch (DateTimeParseException ignored) {
            return LocalDate.parse(value).atStartOfDay().toInstant(ZoneOffset.UTC);
        }
    }

    public static long priceToCents(BigDecimal base, BigDecimal tax) {
        BigDecimal total = (base == null ? BigDecimal.ZERO : base)
                .add(tax == null ? BigDecimal.ZERO : tax);
        return total.movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();
    }
}
