package com.giojo.flightdata.flight;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.giojo.flightdata.flight.dto.CrazySupplierRequest;
import com.giojo.flightdata.flight.dto.CrazySupplierResponse;

@Service
public class CrazySupplierClient {

    private static final Logger log = LoggerFactory.getLogger(CrazySupplierClient.class);

    private final RestClient http;

    public CrazySupplierClient(@Qualifier("crazySupplierRestClient") RestClient http) {
        this.http = http;
    }

    public List<CrazySupplierResponse> search(CrazySupplierRequest flightSearchRequest) {
        if (flightSearchRequest.isEmpty()) {
            return new ArrayList<>();
        }

        List<CrazySupplierResponse> results = new ArrayList<>();
        try {
            results.addAll(http.post()
                    .uri("/flights")
                    .body(flightSearchRequest)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        String body = response.getBody() != null ? new String(response.getBody().readAllBytes()) : "";
                        throw new IllegalStateException("CrazySupplier error %s: %s"
                                .formatted(response.getStatusCode(), body));
                    })
                    .body(new ParameterizedTypeReference<List<CrazySupplierResponse>>() {
                    }));
        } catch (Exception ex) {
            log.warn("Coudln't fetch data from CrazySupplier: {}", ex);
        }

        return results;
    }
}