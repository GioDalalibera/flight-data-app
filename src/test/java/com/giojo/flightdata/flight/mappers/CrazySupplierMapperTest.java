package com.giojo.flightdata.flight.mappers;

import static com.giojo.flightdata.flight.mappers.CrazySupplierMapper.parseDateTimeToUtc;
import static com.giojo.flightdata.flight.mappers.CrazySupplierMapper.priceToCents;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CrazySupplierMapperTest {

        CrazySupplierMapper crazySupplierMapper;

        @BeforeEach
        public void setup() {
                crazySupplierMapper = new CrazySupplierMapper();
        }

        @Test
        public void parseDateTimeToUtcTest() {
                assertAll(
                                () -> assertEquals("2025-10-10T00:00:00Z", parseDateTimeToUtc("2025-10-10").toString()),
                                () -> assertEquals("2025-10-10T23:59:59Z",
                                                parseDateTimeToUtc("2025-10-10T23:59:59").toString()),
                                () -> assertEquals("2025-10-11T00:00:00Z",
                                                parseDateTimeToUtc("2025-10-11T00:00:00").toString()),
                                () -> assertEquals("2025-10-10T12:00:00Z",
                                                parseDateTimeToUtc("2025-10-10T12:00:00").toString()));

        }

        @Test
        public void priceToCentsRoundingTest() {
                assertAll(
                                () -> assertEquals(20010L, priceToCents(new BigDecimal(200), new BigDecimal(.10))),
                                () -> assertEquals(20010L, priceToCents(new BigDecimal(200), new BigDecimal(.101))),
                                () -> assertEquals(20010L, priceToCents(new BigDecimal(200), new BigDecimal(.1049))),
                                () -> assertEquals(20011L, priceToCents(new BigDecimal(200), new BigDecimal(.1051))),
                                () -> assertEquals(20011L, priceToCents(new BigDecimal(200), new BigDecimal(.109))));
        }

        @Test
        public void priceToCentsRoundingNullValues() {
                assertAll(
                                () -> assertEquals(20000, priceToCents(new BigDecimal(200), null)),
                                () -> assertEquals(1000L, priceToCents(null, new BigDecimal(10))),
                                () -> assertEquals(0L, priceToCents(null, null)));
        }

}
