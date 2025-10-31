package com.giojo.flightdata.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class HttpClientConfig {

    @Value("${crazy-supplier.base-url}")
    String crazySupplierUrl;

    @Bean
    @Qualifier("crazySupplierRestClient")
    RestClient crazySupplierRestClient() {
        var factory = new org.springframework.http.client.JdkClientHttpRequestFactory();
        factory.setReadTimeout(Duration.ofSeconds(10));

        return RestClient.builder()
                .baseUrl(crazySupplierUrl)
                .requestFactory(factory)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}