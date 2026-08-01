package com.beyondsignal.game.bridge;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class BridgeCrewAssignments {
    private final EnumMap<BridgeStation, UUID> assignments = new EnumMap<>(BridgeStation.class);

    public synchronized void assign(BridgeStation station, UUID playerId) {
        assignments.values().removeIf(playerId::equals);
        assignments.put(station, playerId);
    }

    public synchronized void vacate(BridgeStation station) {
        assignments.remove(station);
    }

    public synchronized boolean authorized(BridgeStation station, UUID playerId) {
        return playerId.equals(assignments.get(station));
    }

    public synchronized Optional<UUID> occupant(BridgeStation station) {
        return Optional.ofNullable(assignments.get(station));
    }

    public synchronized Map<BridgeStation, UUID> snapshot() {
        return Map.copyOf(assignments);
    }
}
