package com.beyondsignal.game.combat.projectile;

public final class ProjectileFlightModel {
    private final double unitsPerTick;
    private final int minimumTravelTicks;
    private final int maximumTravelTicks;

    public ProjectileFlightModel() {
        this(250.0, 2, 80);
    }

    public ProjectileFlightModel(
        double unitsPerTick,
        int minimumTravelTicks,
        int maximumTravelTicks
    ) {
        if (!Double.isFinite(unitsPerTick) || unitsPerTick <= 0.0) {
            throw new IllegalArgumentException("unitsPerTick must be positive");
        }
        if (minimumTravelTicks < 1 || maximumTravelTicks < minimumTravelTicks) {
            throw new IllegalArgumentException("invalid travel tick limits");
        }
        this.unitsPerTick = unitsPerTick;
        this.minimumTravelTicks = minimumTravelTicks;
        this.maximumTravelTicks = maximumTravelTicks;
    }

    public int travelTicks(double distance) {
        if (!Double.isFinite(distance) || distance < 0.0) {
            throw new IllegalArgumentException("distance cannot be negative");
        }
        int calculated = (int) Math.ceil(distance / unitsPerTick);
        return Math.max(
            minimumTravelTicks,
            Math.min(maximumTravelTicks, calculated)
        );
    }
}
