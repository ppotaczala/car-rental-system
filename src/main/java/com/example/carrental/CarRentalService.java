package com.example.carrental;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CarRentalService {

    private final CarInventory inventory;
    private final Map<CarType, List<Reservation>> reservationsByType = new EnumMap<>(CarType.class);

    public CarRentalService(CarInventory inventory) {
        this.inventory = Objects.requireNonNull(inventory, "inventory must not be null");

        for (CarType type : CarType.values()) {
            reservationsByType.put(type, new ArrayList<>());
        }
    }

    public boolean reserve(CarType type, LocalDateTime start, int days) {
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(start, "start must not be null");

        Reservation newReservation = new Reservation(type, start, days);
        List<Reservation> reservationsForType = reservationsByType.get(type);

        long overlappingReservations = reservationsForType.stream()
                .filter(reservation -> reservation.overlaps(newReservation))
                .count();

        if (overlappingReservations >= inventory.getLimit(type)) {
            return false;
        }

        reservationsForType.add(newReservation);
        return true;
    }

    public List<Reservation> getReservations() {
        return reservationsByType.values().stream()
                .flatMap(List::stream)
                .toList();
    }
}