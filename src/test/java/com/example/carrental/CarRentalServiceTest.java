package com.example.carrental;

import com.example.carrental.pricing.PricingStrategy;
import com.example.carrental.pricing.StandardPricingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
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

        PricingStrategy pricingStrategy = new StandardPricingStrategy(Map.of(
                CarType.SEDAN, BigDecimal.valueOf(100),
                CarType.SUV, BigDecimal.valueOf(150),
                CarType.VAN, BigDecimal.valueOf(200)
        ));

        service = new CarRentalService(inventory, pricingStrategy);
    }

    @Test
    @DisplayName("should create reservation with quote when car is available")
    void shouldCreateReservationWithQuoteWhenCarIsAvailable() {
        Optional<ReservationQuote> result = service.reserve(CarType.SEDAN, START, 2);

        assertTrue(result.isPresent());

        ReservationQuote quote = result.orElseThrow();
        Reservation reservation = quote.reservation();

        assertNotNull(reservation.id());
        assertEquals(CarType.SEDAN, reservation.carType());
        assertEquals(START, reservation.start());
        assertEquals(2, reservation.numberOfDays());
        assertEquals(START.plusDays(2), reservation.end());
        assertEquals(BigDecimal.valueOf(200), quote.totalPrice());

        assertEquals(1, service.getReservations().size());
        assertTrue(service.getReservations().contains(reservation));
    }

    @Test
    @DisplayName("should reject reservation when limit is exceeded")
    void shouldRejectReservationWhenLimitIsExceeded() {
        Optional<ReservationQuote> first = service.reserve(CarType.SEDAN, START, 2);
        Optional<ReservationQuote> second = service.reserve(CarType.SEDAN, START, 2);

        assertTrue(first.isPresent());
        assertTrue(second.isEmpty());
        assertEquals(1, service.getReservations().size());
    }

    @Test
    @DisplayName("should allow reservations for different car types independently")
    void shouldAllowReservationsForDifferentCarTypesIndependently() {
        Optional<ReservationQuote> suvReservation = service.reserve(CarType.SUV, START, 2);
        Optional<ReservationQuote> sedanReservation = service.reserve(CarType.SEDAN, START, 2);

        assertTrue(suvReservation.isPresent());
        assertTrue(sedanReservation.isPresent());

        assertEquals(BigDecimal.valueOf(300), suvReservation.orElseThrow().totalPrice());
        assertEquals(BigDecimal.valueOf(200), sedanReservation.orElseThrow().totalPrice());
        assertEquals(2, service.getReservations().size());
    }

    @Test
    @DisplayName("should allow reservation when periods are separated")
    void shouldAllowReservationWhenPeriodsAreSeparated() {
        Optional<ReservationQuote> first = service.reserve(CarType.VAN, START, 2);
        Optional<ReservationQuote> second = service.reserve(CarType.VAN, START.plusDays(3), 1);

        assertTrue(first.isPresent());
        assertTrue(second.isPresent());

        assertEquals(BigDecimal.valueOf(400), first.orElseThrow().totalPrice());
        assertEquals(BigDecimal.valueOf(200), second.orElseThrow().totalPrice());
        assertEquals(2, service.getReservations().size());
    }

    @Test
    @DisplayName("should allow reservation when one ends exactly when another starts")
    void shouldAllowReservationWhenOneEndsExactlyWhenAnotherStarts() {
        Optional<ReservationQuote> first = service.reserve(CarType.VAN, START, 2);
        Optional<ReservationQuote> second = service.reserve(CarType.VAN, START.plusDays(2), 1);

        assertTrue(first.isPresent());
        assertTrue(second.isPresent());
        assertEquals(2, service.getReservations().size());
    }

    @Test
    @DisplayName("should reject reservation when periods overlap partially")
    void shouldRejectReservationWhenPeriodsOverlapPartially() {
        Optional<ReservationQuote> first = service.reserve(CarType.SEDAN, START, 3);
        Optional<ReservationQuote> second = service.reserve(CarType.SEDAN, START.plusDays(1), 2);

        assertTrue(first.isPresent());
        assertTrue(second.isEmpty());
        assertEquals(1, service.getReservations().size());
    }

    @Test
    @DisplayName("should reject reservation when new reservation fully wraps existing reservation")
    void shouldRejectReservationWhenNewReservationFullyWrapsExistingReservation() {
        Optional<ReservationQuote> first = service.reserve(CarType.SEDAN, START.plusDays(1), 1);
        Optional<ReservationQuote> second = service.reserve(CarType.SEDAN, START, 3);

        assertTrue(first.isPresent());
        assertTrue(second.isEmpty());
        assertEquals(1, service.getReservations().size());
    }

    @Test
    @DisplayName("should calculate total price based on daily rate and number of days")
    void shouldCalculateTotalPriceBasedOnDailyRateAndNumberOfDays() {
        Optional<ReservationQuote> result = service.reserve(CarType.SUV, START, 3);

        assertTrue(result.isPresent());
        assertEquals(BigDecimal.valueOf(450), result.orElseThrow().totalPrice());
    }

    @Test
    @DisplayName("should assign unique ids to different reservations")
    void shouldAssignUniqueIdsToDifferentReservations() {
        Optional<ReservationQuote> first = service.reserve(CarType.SEDAN, START, 1);
        Optional<ReservationQuote> second = service.reserve(CarType.SUV, START, 1);

        assertTrue(first.isPresent());
        assertTrue(second.isPresent());

        assertNotEquals(
                first.orElseThrow().reservation().id(),
                second.orElseThrow().reservation().id()
        );
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
    void shouldRejectNullStartDate() {
        assertThrows(NullPointerException.class, () ->
                service.reserve(CarType.SEDAN, null, 1)
        );
    }
}