package com.example.carrental;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CarRentalService {

    private final CarInventory inventory;
    private final List<Reservation> reservations = new ArrayList<>();

    public CarRentalService(CarInventory inventory) {
        this.inventory = Objects.requireNonNull(inventory, "inventory must not be null");
    }

    public boolean reserve(CarType type, LocalDateTime start, int days) {
        Reservation newReservation = new Reservation(type, start, days);

        long overlappingReservations = reservations.stream()
                .filter(reservation -> reservation.getCarType() == type)
                .filter(reservation -> reservation.overlaps(newReservation))
                .count();

        int limit = inventory.getLimit(type);
        if (overlappingReservations >= limit) {
            return false;
        }

        reservations.add(newReservation);
        return true;
    }

    public List<Reservation> getReservations() {
        return List.copyOf(reservations);
    }
}