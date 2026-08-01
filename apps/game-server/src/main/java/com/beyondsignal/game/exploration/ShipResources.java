package com.beyondsignal.game.exploration;

public record ShipResources(
    int fuel,
    int maximumFuel,
    int hullIntegrity,
    int maximumHull,
    int credits
) {
    public ShipResources {
        if (maximumFuel < 1 || maximumHull < 1) {
            throw new IllegalArgumentException("maximum values must be positive");
        }
        if (fuel < 0 || fuel > maximumFuel) {
            throw new IllegalArgumentException("fuel is outside valid range");
        }
        if (hullIntegrity < 0 || hullIntegrity > maximumHull) {
            throw new IllegalArgumentException("hullIntegrity is outside valid range");
        }
        if (credits < 0) {
            throw new IllegalArgumentException("credits cannot be negative");
        }
    }

    public static ShipResources initial() {
        return new ShipResources(100, 100, 100, 100, 1_000);
    }

    public ShipResources consumeFuel(int amount) {
        if (amount < 0 || amount > fuel) {
            throw new IllegalArgumentException("Invalid fuel consumption");
        }
        return new ShipResources(fuel - amount, maximumFuel, hullIntegrity, maximumHull, credits);
    }

    public ShipResources refuel(int amount, int cost) {
        if (amount < 0 || cost < 0 || cost > credits) {
            throw new IllegalArgumentException("Invalid refuel transaction");
        }
        return new ShipResources(
            Math.min(maximumFuel, fuel + amount),
            maximumFuel,
            hullIntegrity,
            maximumHull,
            credits - cost
        );
    }

    public ShipResources damage(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("damage cannot be negative");
        }
        return new ShipResources(
            fuel, maximumFuel, Math.max(0, hullIntegrity - amount), maximumHull, credits
        );
    }

    public ShipResources repair(int amount, int cost) {
        if (amount < 0 || cost < 0 || cost > credits) {
            throw new IllegalArgumentException("Invalid repair transaction");
        }
        return new ShipResources(
            fuel,
            maximumFuel,
            Math.min(maximumHull, hullIntegrity + amount),
            maximumHull,
            credits - cost
        );
    }

    public ShipResources credit(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        }
        return new ShipResources(fuel, maximumFuel, hullIntegrity, maximumHull, credits + amount);
    }

    public ShipResources debit(int amount) {
        if (amount < 0 || amount > credits) {
            throw new IllegalArgumentException("Invalid debit amount");
        }
        return new ShipResources(fuel, maximumFuel, hullIntegrity, maximumHull, credits - amount);
    }
}
