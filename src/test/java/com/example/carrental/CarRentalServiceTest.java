package com.example.carrental;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CarRentalServiceTest {

    private CarRentalService service;

    @BeforeEach
    void setUp() {
        CarInventory inventory = new CarInventory(Map.of(
                CarType.SEDAN, 1,
                CarType.SUV, 1,
                CarType.VAN, 1
        ));
        service = new CarRentalService(inventory);
    }

    @Test
    void shouldReserveWhenAvailable() {
        boolean result = service.reserve(
                CarType.SEDAN,
                LocalDateTime.of(2026, 3, 21, 10, 0),
                2
        );

        assertTrue(result);
        assertEquals(1, service.getReservations().size());
    }

    @Test
    void shouldRejectWhenLimitExceeded() {
        LocalDateTime start = LocalDateTime.of(2026, 3, 21, 10, 0);

        assertTrue(service.reserve(CarType.SEDAN, start, 2));
        assertFalse(service.reserve(CarType.SEDAN, start, 2));
    }

    @Test
    void shouldAllowReservationsForDifferentCarTypesIndependently() {
        LocalDateTime start = LocalDateTime.of(2026, 3, 21, 10, 0);

        assertTrue(service.reserve(CarType.SUV, start, 2));
        assertTrue(service.reserve(CarType.SEDAN, start, 2));
    }

    @Test
    void shouldAllowReservationWhenPeriodsDoNotOverlap() {
        LocalDateTime start = LocalDateTime.of(2026, 3, 21, 10, 0);

        assertTrue(service.reserve(CarType.VAN, start, 2));
        assertTrue(service.reserve(CarType.VAN, start.plusDays(2), 1));
    }

    @Test
    void shouldRejectInvalidNumberOfDays() {
        LocalDateTime start = LocalDateTime.of(2026, 3, 21, 10, 0);

        assertThrows(IllegalArgumentException.class, () ->
                service.reserve(CarType.SEDAN, start, 0)
        );
    }

    @Test
    void shouldRejectNullCarType() {
        LocalDateTime start = LocalDateTime.of(2026, 3, 21, 10, 0);

        assertThrows(NullPointerException.class, () ->
                service.reserve(null, start, 1)
        );
    }
}