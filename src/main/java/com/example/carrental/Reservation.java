package com.example.carrental;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class Reservation {

    private final CarType carType;
    private final LocalDateTime start;
    private final int numberOfDays;

    public Reservation(CarType carType, LocalDateTime start, int numberOfDays) {
        this.carType = Objects.requireNonNull(carType, "carType must not be null");
        this.start = Objects.requireNonNull(start, "start must not be null");

        if (numberOfDays <= 0) {
            throw new IllegalArgumentException("numberOfDays must be > 0");
        }

        this.numberOfDays = numberOfDays;
    }

    public LocalDateTime getEnd() {
        return start.plusDays(numberOfDays);
    }

    public boolean overlaps(Reservation other) {
        Objects.requireNonNull(other, "other must not be null");

        return start.isBefore(other.getEnd()) &&
                other.getStart().isBefore(getEnd());
    }
}