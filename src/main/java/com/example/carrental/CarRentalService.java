package com.example.carrental;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

public class CarRentalService {

    private final CarInventory inventory;
    private final Map<CarType, List<Reservation>> reservationsByType = new EnumMap<>(CarType.class);
    private final Map<CarType, ReentrantLock> locksByType = new EnumMap<>(CarType.class);

    public CarRentalService(CarInventory inventory) {
        this.inventory = Objects.requireNonNull(inventory, "inventory must not be null");

        for (CarType type : CarType.values()) {
            reservationsByType.put(type, new ArrayList<>());
            locksByType.put(type, new ReentrantLock());
        }
    }

    public Optional<Reservation> reserve(CarType type, LocalDateTime start, int days) {
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(start, "start must not be null");

        Reservation newReservation = new Reservation(UUID.randomUUID(), type, start, days);
        ReentrantLock lock = locksByType.get(type);

        lock.lock();
        try {
            List<Reservation> reservationsForType = reservationsByType.get(type);

            long overlappingReservations = reservationsForType.stream()
                    .filter(existingReservation -> existingReservation.overlaps(newReservation))
                    .count();

            if (overlappingReservations >= inventory.getLimit(type)) {
                return Optional.empty();
            }

            reservationsForType.add(newReservation);
            return Optional.of(newReservation);
        } finally {
            lock.unlock();
        }
    }

    public List<Reservation> getReservations() {
        return reservationsByType.values().stream()
                .flatMap(List::stream)
                .toList();
    }

    public List<Reservation> getReservationsByType(CarType type) {
        Objects.requireNonNull(type, "type must not be null");

        ReentrantLock lock = locksByType.get(type);
        lock.lock();
        try {
            return List.copyOf(reservationsByType.get(type));
        } finally {
            lock.unlock();
        }
    }
}