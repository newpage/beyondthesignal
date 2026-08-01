package com.beyondsignal.game.combat.engine;

public record HitResolution(double probability, double roll, boolean hit) {
    public HitResolution {
        if (!Double.isFinite(probability) || probability < 0.0 || probability > 1.0) {
            throw new IllegalArgumentException("probability must be between 0.0 and 1.0");
        }
        if (!Double.isFinite(roll) || roll < 0.0 || roll >= 1.0) {
            throw new IllegalArgumentException("roll must be between 0.0 inclusive and 1.0 exclusive");
        }
        hit = roll < probability;
    }
}
