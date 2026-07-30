package com.beyondsignal.game.simulation;

public record Vector3(double x, double y, double z) {
    public static final Vector3 ZERO = new Vector3(0.0, 0.0, 0.0);

    public Vector3 {
        requireFinite(x, "x");
        requireFinite(y, "y");
        requireFinite(z, "z");
    }

    public Vector3 add(Vector3 other) {
        return new Vector3(x + other.x, y + other.y, z + other.z);
    }

    public Vector3 scale(double factor) {
        requireFinite(factor, "factor");
        return new Vector3(x * factor, y * factor, z * factor);
    }

    private static void requireFinite(double value, String name) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException(name + " must be finite");
        }
    }
}
