package com.example.carrental;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CarRentalServiceTest {

    private static final LocalDateTime START = LocalDateTime.of(2026, 3, 21, 10, 0);

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
    @DisplayName("should create reservation when car is available")
    void shouldReserveWhenAvailable() {
        Optional<Reservation> result = service.reserve(CarType.SEDAN, START, 2);

        assertTrue(result.isPresent());

        Reservation reservation = result.orElseThrow();
        assertNotNull(reservation.id());
        assertEquals(CarType.SEDAN, reservation.carType());
        assertEquals(START, reservation.start());
        assertEquals(2, reservation.numberOfDays());
        assertEquals(1, service.getReservations().size());
    }

    @Test
    @DisplayName("should reject reservation when limit is exceeded")
    void shouldRejectWhenLimitExceeded() {
        Optional<Reservation> first = service.reserve(CarType.SEDAN, START, 2);
        Optional<Reservation> second = service.reserve(CarType.SEDAN, START, 2);

        assertTrue(first.isPresent());
        assertTrue(second.isEmpty());
        assertEquals(1, service.getReservations().size());
    }

    @Test
    @DisplayName("should allow reservations for different car types independently")
    void shouldAllowReservationsForDifferentCarTypesIndependently() {
        Optional<Reservation> suvReservation = service.reserve(CarType.SUV, START, 2);
        Optional<Reservation> sedanReservation = service.reserve(CarType.SEDAN, START, 2);

        assertTrue(suvReservation.isPresent());
        assertTrue(sedanReservation.isPresent());
        assertEquals(2, service.getReservations().size());
    }

    @Test
    @DisplayName("should allow reservation when periods are separated")
    void shouldAllowReservationWhenPeriodsAreSeparated() {
        Optional<Reservation> first = service.reserve(CarType.VAN, START, 2);
        Optional<Reservation> second = service.reserve(CarType.VAN, START.plusDays(3), 1);

        assertTrue(first.isPresent());
        assertTrue(second.isPresent());
        assertEquals(2, service.getReservations().size());
    }

    @Test
    @DisplayName("should allow reservation when one ends exactly when another starts")
    void shouldAllowReservationWhenOneEndsExactlyWhenAnotherStarts() {
        Optional<Reservation> first = service.reserve(CarType.VAN, START, 2);
        Optional<Reservation> second = service.reserve(CarType.VAN, START.plusDays(2), 1);

        assertTrue(first.isPresent());
        assertTrue(second.isPresent());
        assertEquals(2, service.getReservations().size());
    }

    @Test
    @DisplayName("should reject reservation when periods overlap partially")
    void shouldRejectWhenReservationsOverlapPartially() {
        Optional<Reservation> first = service.reserve(CarType.SEDAN, START, 3);
        Optional<Reservation> second = service.reserve(CarType.SEDAN, START.plusDays(1), 2);

        assertTrue(first.isPresent());
        assertTrue(second.isEmpty());
        assertEquals(1, service.getReservations().size());
    }

    @Test
    @DisplayName("should reject reservation when new reservation fully wraps existing reservation")
    void shouldRejectWhenNewReservationWrapsExistingReservation() {
        Optional<Reservation> first = service.reserve(CarType.SEDAN, START.plusDays(1), 1);
        Optional<Reservation> second = service.reserve(CarType.SEDAN, START, 3);

        assertTrue(first.isPresent());
        assertTrue(second.isEmpty());
        assertEquals(1, service.getReservations().size());
    }

    @Test
    @DisplayName("should reject invalid number of days")
    void shouldRejectInvalidNumberOfDays() {
        assertThrows(IllegalArgumentException.class, () ->
                service.reserve(CarType.SEDAN, START, 0)
        );
    }

    @Test
    @DisplayName("should reject null car type")
    void shouldRejectNullCarType() {
        assertThrows(NullPointerException.class, () ->
                service.reserve(null, START, 1)
        );
    }

    @Test
    @DisplayName("should reject null start date")
    void shouldRejectNullStart() {
        assertThrows(NullPointerException.class, () ->
                service.reserve(CarType.SEDAN, null, 1)
        );
    }
}