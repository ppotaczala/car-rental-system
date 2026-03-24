package com.example.carrental;

import com.example.carrental.pricing.PricingStrategy;
import com.example.carrental.repository.ReservationRepository;

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
    private final ReservationRepository reservationRepository;
    private final Map<CarType, List<Reservation>> reservationsByType = new EnumMap<>(CarType.class);

    public CarRentalService(CarInventory inventory, PricingStrategy pricingStrategy, ReservationRepository reservationRepository) {
        this.inventory = Objects.requireNonNull(inventory, "inventory must not be null");
        this.pricingStrategy = Objects.requireNonNull(pricingStrategy, "pricingStrategy must not be null");
        this.reservationRepository = reservationRepository;

        for (CarType type : CarType.values()) {
            reservationsByType.put(type, new ArrayList<>());
        }
    }

    public Optional<ReservationQuote> reserve(CarType type, LocalDateTime start, int days) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(start);

        LocalDateTime end = start.plusDays(days);

        long overlapping = reservationRepository.countOverlapping(type, start, end);
        int limit = inventory.getLimit(type);

        if (overlapping >= limit) {
            return Optional.empty();
        }

        Reservation reservation = new Reservation(
                UUID.randomUUID(),
                type,
                start,
                days
        );

        reservationRepository.save(reservation);

        BigDecimal totalPrice = pricingStrategy.calculatePrice(reservation);
        return Optional.of(new ReservationQuote(reservation, totalPrice));
    }

    public List<Reservation> getReservations() {
        return reservationsByType.values().stream()
                .flatMap(List::stream)
                .toList();
    }
}