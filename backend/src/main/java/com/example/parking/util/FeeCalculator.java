package com.example.parking.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class FeeCalculator {

    private FeeCalculator() {
    }

    public static BigDecimal calculate(int durationMinutes) {
        if (durationMinutes <= 30) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        int billableMinutes = durationMinutes - 30;
        int hours = (int) Math.ceil(billableMinutes / 60.0);
        BigDecimal amount = BigDecimal.valueOf(hours).multiply(new BigDecimal("5.00"));
        BigDecimal dailyMax = new BigDecimal("30.00");
        if (amount.compareTo(dailyMax) > 0) {
            amount = dailyMax;
        }
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}
