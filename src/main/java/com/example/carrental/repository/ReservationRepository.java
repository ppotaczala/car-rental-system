package com.example.carrental.repository;

import com.example.carrental.CarType;
import com.example.carrental.Reservation;

import java.time.LocalDateTime;

public interface ReservationRepository {
    long countOverlapping(CarType type, LocalDateTime start, LocalDateTime end);
    void save(Reservation reservation);
}