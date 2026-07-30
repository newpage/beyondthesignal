package com.beyondsignal.game.navigation;

import java.time.Duration;

public final class WarpCalculator {
    private static final double HOURS_PER_LIGHT_YEAR_AT_WARP_ONE = 8_766.0;

    public Duration estimateTravelTime(double distanceLightYears, int warpFactor) {
        validate(distanceLightYears, warpFactor);

        // Alpha 2 gameplay scale: effective velocity grows with warpFactor cubed.
        double hours = distanceLightYears * HOURS_PER_LIGHT_YEAR_AT_WARP_ONE
            / Math.pow(warpFactor, 3);

        long seconds = Math.max(1L, Math.round(hours * 3_600.0));
        return Duration.ofSeconds(seconds);
    }

    public double distanceTravelled(Duration elapsed, int warpFactor) {
        if (elapsed == null || elapsed.isNegative()) {
            throw new IllegalArgumentException("elapsed must be non-negative");
        }
        validate(0.0, warpFactor);
        double hours = elapsed.toSeconds() / 3_600.0;
        return hours * Math.pow(warpFactor, 3) / HOURS_PER_LIGHT_YEAR_AT_WARP_ONE;
    }

    private static void validate(double distanceLightYears, int warpFactor) {
        if (distanceLightYears < 0.0 || !Double.isFinite(distanceLightYears)) {
            throw new IllegalArgumentException("distanceLightYears must be finite and non-negative");
        }
        if (warpFactor < 1 || warpFactor > 9) {
            throw new IllegalArgumentException("warpFactor must be between 1 and 9");
        }
    }
}
