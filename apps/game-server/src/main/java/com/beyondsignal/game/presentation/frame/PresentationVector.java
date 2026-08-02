package com.beyondsignal.game.presentation.frame;

public record PresentationVector(double x, double y, double z) {
    public PresentationVector {
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)) {
            throw new IllegalArgumentException("vector components must be finite");
        }
    }
}
