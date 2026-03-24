package com.example.carrental.repository;

import com.example.carrental.CarType;
import com.example.carrental.Reservation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryReservationRepository implements ReservationRepository {

    private final Map<CarType, List<Reservation>> reservationsByType = new HashMap<>();

    @Override
    public long countOverlapping(CarType type, LocalDateTime start, LocalDateTime end) {
        return reservationsByType.getOrDefault(type, List.of()).stream()
                .filter(existing -> overlaps(existing, start, end))
                .count();
    }

    private boolean overlaps(Reservation existing, LocalDateTime start, LocalDateTime end) {
        return start.isBefore(existing.end()) && existing.start().isBefore(end);
    }

    @Override
    public void save(Reservation reservation) {
        reservationsByType
                .computeIfAbsent(reservation.carType(), k -> new ArrayList<>())
                .add(reservation);
    }
}