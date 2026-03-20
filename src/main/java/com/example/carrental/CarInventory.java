package com.example.carrental;

import java.util.Map;
import java.util.Objects;

public record CarInventory(Map<CarType, Integer> limits) {

    public CarInventory {
        Objects.requireNonNull(limits, "limits must not be null");
    }

    public int getLimit(CarType type) {
        Integer limit = limits.get(type);
        if (limit == null) {
            throw new IllegalArgumentException("No limit defined for car type: " + type);
        }
        return limit;
    }
}