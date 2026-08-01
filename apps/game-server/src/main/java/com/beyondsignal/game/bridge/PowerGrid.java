package com.beyondsignal.game.bridge;

import java.util.EnumMap;
import java.util.Map;

public final class PowerGrid {
    private final EnumMap<Subsystem, Integer> allocations = new EnumMap<>(Subsystem.class);

    public PowerGrid() {
        allocations.put(Subsystem.ENGINES, 25);
        allocations.put(Subsystem.SHIELDS, 20);
        allocations.put(Subsystem.WEAPONS, 20);
        allocations.put(Subsystem.SENSORS, 20);
        allocations.put(Subsystem.LIFE_SUPPORT, 15);
    }

    public synchronized void allocate(Subsystem subsystem, int amount) {
        if (amount < 0 || amount > 100) {
            throw new IllegalArgumentException("Power allocation must be between 0 and 100");
        }
        int existing = allocations.get(subsystem);
        int proposedTotal = total() - existing + amount;
        if (proposedTotal > 100) {
            throw new IllegalStateException("Total power allocation cannot exceed 100");
        }
        allocations.put(subsystem, amount);
    }

    public synchronized int allocation(Subsystem subsystem) {
        return allocations.get(subsystem);
    }

    public synchronized int total() {
        return allocations.values().stream().mapToInt(Integer::intValue).sum();
    }

    public synchronized Map<Subsystem, Integer> snapshot() {
        return Map.copyOf(allocations);
    }
}
