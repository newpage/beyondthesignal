package com.beyondsignal.game.combat.ai.fleet.formation;

public record FormationVector(double x, double y, double z) {
    public FormationVector {
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)) {
            throw new IllegalArgumentException("Formation coordinates must be finite");
        }
    }

    public FormationVector add(FormationVector other) {
        return new FormationVector(x + other.x, y + other.y, z + other.z);
    }

    public FormationVector subtract(FormationVector other) {
        return new FormationVector(x - other.x, y - other.y, z - other.z);
    }

    public FormationVector scale(double factor) {
        if (!Double.isFinite(factor)) {
            throw new IllegalArgumentException("factor must be finite");
        }
        return new FormationVector(x * factor, y * factor, z * factor);
    }

    public double distanceTo(FormationVector other) {
        double dx = x - other.x;
        double dy = y - other.y;
        double dz = z - other.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public static FormationVector zero() {
        return new FormationVector(0.0, 0.0, 0.0);
    }
}
