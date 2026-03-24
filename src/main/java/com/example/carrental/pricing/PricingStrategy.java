package com.example.carrental.pricing;


import com.example.carrental.Reservation;

import java.math.BigDecimal;

public interface PricingStrategy {
    BigDecimal calculatePrice(Reservation reservation);
}