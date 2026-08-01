package com.beyondsignal.game.exploration;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public final class CargoHold {
    private final int capacity;
    private final EnumMap<ResourceType, Integer> inventory = new EnumMap<>(ResourceType.class);

    public CargoHold(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        this.capacity = capacity;
        for (ResourceType type : ResourceType.values()) {
            inventory.put(type, 0);
        }
    }

    public synchronized int quantity(ResourceType type) {
        return inventory.get(type);
    }

    public synchronized int usedCapacity() {
        return inventory.values().stream().mapToInt(Integer::intValue).sum();
    }

    public synchronized int availableCapacity() {
        return capacity - usedCapacity();
    }

    public synchronized void add(ResourceType type, int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        if (quantity > availableCapacity()) {
            throw new IllegalStateException("Insufficient cargo capacity");
        }
        inventory.put(type, inventory.get(type) + quantity);
    }

    public synchronized void remove(ResourceType type, int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        int current = inventory.get(type);
        if (quantity > current) {
            throw new IllegalStateException("Insufficient cargo");
        }
        inventory.put(type, current - quantity);
    }

    public synchronized Map<ResourceType, Integer> snapshot() {
        return Collections.unmodifiableMap(new EnumMap<>(inventory));
    }
}
