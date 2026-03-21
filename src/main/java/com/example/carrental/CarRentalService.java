package com.example.carrental;

import com.example.carrental.pricing.PricingStrategy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class CarRentalService {

    private final CarInventory inventory;
    private final PricingStrategy pricingStrategy;
    private final Map<CarType, List<Reservation>> reservationsByType = new EnumMap<>(CarType.class);

    public CarRentalService(CarInventory inventory, PricingStrategy pricingStrategy) {
        this.inventory = Objects.requireNonNull(inventory, "inventory must not be null");
        this.pricingStrategy = Objects.requireNonNull(pricingStrategy, "pricingStrategy must not be null");

        for (CarType type : CarType.values()) {
            reservationsByType.put(type, new ArrayList<>());
        }
    }

    public Optional<ReservationQuote> reserve(CarType type, LocalDateTime start, int days) {
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(start, "start must not be null");

        Reservation newReservation = new Reservation(
                UUID.randomUUID(),
                type,
                start,
                days
        );

        List<Reservation> reservationsForType = reservationsByType.get(type);

        long overlappingReservations = reservationsForType.stream()
                .filter(existing -> existing.overlaps(newReservation))
                .count();

        int limit = inventory.getLimit(type);
        if (overlappingReservations >= limit) {
            return Optional.empty();
        }

        reservationsForType.add(newReservation);

        BigDecimal totalPrice = pricingStrategy.calculatePrice(newReservation);
        return Optional.of(new ReservationQuote(newReservation, totalPrice));
    }

    public List<Reservation> getReservations() {
        return reservationsByType.values().stream()
                .flatMap(List::stream)
                .toList();
    }
}