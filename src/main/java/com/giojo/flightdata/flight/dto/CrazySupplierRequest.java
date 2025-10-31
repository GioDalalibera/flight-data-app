package com.giojo.flightdata.flight.dto;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Pattern;

public record CrazySupplierRequest(
        @Pattern(regexp = "^[A-Za-z]{3}$") String from,
        @Pattern(regexp = "^[A-Za-z]{3}$") String to,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String outboundDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String inboundDate) {

    public boolean isEmpty() {
        return null == from && null == to && null == outboundDate && null == inboundDate;
    }
}