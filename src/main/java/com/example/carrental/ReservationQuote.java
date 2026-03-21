package com.example.carrental;

import java.math.BigDecimal;
import java.util.Objects;

public record ReservationQuote(Reservation reservation, BigDecimal totalPrice) {
    public ReservationQuote {
        Objects.requireNonNull(reservation, "reservation must not be null");
        Objects.requireNonNull(totalPrice, "totalPrice must not be null");
    }
}