package com.example.carrental;

import java.lang.annotation.Target;
import java.time.LocalDateTime;

public class Reservation {
    private final CarType carType;
    private final LocalDateTime start;
    private final int days;

    public Reservation(CarType carType, LocalDateTime start, int days) {
        if (carType == null || start == null) {
            throw new IllegalArgumentException("Invalid input");
        }
        if (days <= 0) {
            throw new IllegalArgumentException("Days must be > 0");
        }

        this.carType = carType;
        this.start = start;
        this.days = days;
    }

    public CarType getCarType() { return carType; }
    public LocalDateTime getStart() { return start; }
    public int getDays() { return days; }

    public LocalDateTime getEnd() {
        return start.plusDays(days);
    }

    public boolean overlaps(Reservation other) {
        return start.isBefore(other.getEnd()) &&
                other.getStart().isBefore(getEnd());
    }
}