package com.beyondsignal.game.simulation;

public record SubsystemState(int health, int powerAllocation) {
    public SubsystemState {
        requirePercentage(health, "health");
        requirePercentage(powerAllocation, "powerAllocation");
    }

    public static SubsystemState nominal(int powerAllocation) {
        return new SubsystemState(100, powerAllocation);
    }

    public SubsystemState withPowerAllocation(int value) {
        return new SubsystemState(health, value);
    }

    private static void requirePercentage(int value, String name) {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException(name + " must be between 0 and 100");
        }
    }
}
