package com.beyondsignal.game.combat.ai.explain;

public record DecisionConfidence(double value) {
    public DecisionConfidence {
        if (!Double.isFinite(value) || value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException("value must be between 0.0 and 1.0");
        }
    }

    public int percentage() {
        return (int) Math.round(value * 100.0);
    }
}
