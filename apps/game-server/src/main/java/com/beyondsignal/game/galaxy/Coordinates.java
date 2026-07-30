package com.beyondsignal.game.galaxy;

public record Coordinates(double x, double y, double z) {
    public Coordinates {
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)) {
            throw new IllegalArgumentException("Coordinates must be finite");
        }
    }

    public double distanceTo(Coordinates other) {
        if (other == null) {
            throw new IllegalArgumentException("Other coordinates are required");
        }
        double dx = x - other.x;
        double dy = y - other.y;
        double dz = z - other.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
