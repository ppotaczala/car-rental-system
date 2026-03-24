package com.example.carrental.pricing;

import com.example.carrental.CarType;
import com.example.carrental.Reservation;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class StandardPricingStrategy implements PricingStrategy {

    private final Map<CarType, BigDecimal> dailyRates = new EnumMap<>(CarType.class);

    public StandardPricingStrategy(Map<CarType, BigDecimal> dailyRates) {
        Objects.requireNonNull(dailyRates, "dailyRates must not be null");
        this.dailyRates.putAll(dailyRates);
    }

    @Override
    public BigDecimal calculatePrice(Reservation reservation) {
        Objects.requireNonNull(reservation, "reservation must not be null");

        BigDecimal dailyRate = dailyRates.get(reservation.carType());
        if (dailyRate == null) {
            throw new IllegalArgumentException("No daily rate defined for car type: " + reservation.carType());
        }

        return dailyRate.multiply(BigDecimal.valueOf(reservation.numberOfDays()));
    }
}