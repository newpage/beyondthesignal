package com.beyondsignal.game.combat.ai.maneuver.geometry;

import java.util.Objects;

public record CombatVector(double x, double y, double z) {
    public CombatVector {
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)) {
            throw new IllegalArgumentException("Combat vector components must be finite");
        }
    }

    public CombatVector add(CombatVector other) {
        Objects.requireNonNull(other, "other");
        return new CombatVector(x + other.x, y + other.y, z + other.z);
    }

    public CombatVector subtract(CombatVector other) {
        Objects.requireNonNull(other, "other");
        return new CombatVector(x - other.x, y - other.y, z - other.z);
    }

    public CombatVector scale(double factor) {
        if (!Double.isFinite(factor)) {
            throw new IllegalArgumentException("factor must be finite");
        }
        return new CombatVector(x * factor, y * factor, z * factor);
    }

    public double dot(CombatVector other) {
        Objects.requireNonNull(other, "other");
        return x * other.x + y * other.y + z * other.z;
    }

    public CombatVector cross(CombatVector other) {
        Objects.requireNonNull(other, "other");
        return new CombatVector(
            y * other.z - z * other.y,
            z * other.x - x * other.z,
            x * other.y - y * other.x
        );
    }

    public double magnitude() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public CombatVector normalize() {
        double magnitude = magnitude();
        if (magnitude == 0.0) {
            throw new IllegalStateException("Cannot normalize zero vector");
        }
        return scale(1.0 / magnitude);
    }

    public double distanceTo(CombatVector other) {
        return subtract(other).magnitude();
    }

    public static CombatVector zero() {
        return new CombatVector(0.0, 0.0, 0.0);
    }
}
