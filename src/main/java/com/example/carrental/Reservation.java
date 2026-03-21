package com.example.carrental;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public record Reservation(
        UUID id,
        CarType carType,
        LocalDateTime start,
        int numberOfDays
) {

    public Reservation {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(carType, "carType must not be null");
        Objects.requireNonNull(start, "start must not be null");

        if (numberOfDays <= 0) {
            throw new IllegalArgumentException("numberOfDays must be positive");
        }
    }

    public LocalDateTime end() {
        return start.plusDays(numberOfDays);
    }

    public boolean overlaps(Reservation other) {
        Objects.requireNonNull(other, "other must not be null");

        return start.isBefore(other.end())
                && other.start().isBefore(end());
    }
}