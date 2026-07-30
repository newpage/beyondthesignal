package com.beyondsignal.game.simulation;

import java.util.Objects;
import java.util.UUID;

public record CombatContactState(
    UUID id,
    String displayName,
    int shieldStrength,
    int hullIntegrity,
    boolean destroyed
) {
    public CombatContactState {
        Objects.requireNonNull(id, "id must not be null");
        displayName = Objects.requireNonNull(displayName, "displayName must not be null").trim();
        if (displayName.isEmpty()) {
            throw new IllegalArgumentException("displayName must not be blank");
        }
        if (shieldStrength < 0 || shieldStrength > 100) {
            throw new IllegalArgumentException("shieldStrength must be between 0 and 100");
        }
        if (hullIntegrity < 0 || hullIntegrity > 100) {
            throw new IllegalArgumentException("hullIntegrity must be between 0 and 100");
        }
        if (destroyed != (hullIntegrity == 0)) {
            throw new IllegalArgumentException("destroyed must match zero hull integrity");
        }
    }

    public static CombatContactState trainingDrone(UUID id) {
        return new CombatContactState(id, "Training Drone", 40, 100, false);
    }
}
