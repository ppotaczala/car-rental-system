package com.example.carrental;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    public boolean reserve(CarType type, LocalDateTime start, int days) {
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(start, "start must not be null");

        Reservation newReservation = new Reservation(type, start, days);
        ReentrantLock lock = locksByType.get(type);

        lock.lock();
        try {
            List<Reservation> reservationsForType = reservationsByType.get(type);

            long overlappingReservations = reservationsForType.stream()
                    .filter(reservation -> reservation.overlaps(newReservation))
                    .count();

            if (overlappingReservations >= inventory.getLimit(type)) {
                return false;
            }

            reservationsForType.add(newReservation);
            return true;
        } finally {
            lock.unlock();
        }
    }

    public List<Reservation> getReservations() {
        return reservationsByType.values().stream()
                .flatMap(List::stream)
                .toList();
    }
}