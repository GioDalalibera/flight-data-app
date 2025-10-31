package com.giojo.flightdata.flight.dto;

import java.math.BigDecimal;

public record CrazySupplierResponse(
                String carrier,
                BigDecimal basePrice, // Assuming a decimal value
                BigDecimal tax, // Assuming a decimal value
                String departureAirportName,
                String arrivalAirportName,
                String outboundDateTime,
                String inboundDateTime) {
}