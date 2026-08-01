package com.beyondsignal.game.exploration;

public final class StarbaseService {
    public ShipResources refuel(ShipResources resources, int amount) {
        int actual = Math.min(amount, resources.maximumFuel() - resources.fuel());
        int cost = actual * 4;
        return resources.refuel(actual, cost);
    }

    public ShipResources repair(ShipResources resources, int amount) {
        int actual = Math.min(amount, resources.maximumHull() - resources.hullIntegrity());
        int cost = actual * 6;
        return resources.repair(actual, cost);
    }
}
