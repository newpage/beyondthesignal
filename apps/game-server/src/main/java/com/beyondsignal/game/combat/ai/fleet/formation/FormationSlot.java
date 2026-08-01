package com.beyondsignal.game.combat.ai.fleet.formation;

import com.beyondsignal.game.combat.ai.fleet.FleetRole;
import java.util.Objects;

public record FormationSlot(
    int index,
    String name,
    FormationVector offset,
    FleetRole preferredRole,
    int priority
) {
    public FormationSlot {
        if (index < 0) {
            throw new IllegalArgumentException("index cannot be negative");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        offset = Objects.requireNonNull(offset, "offset");
        preferredRole = Objects.requireNonNull(preferredRole, "preferredRole");
        if (priority < 0) {
            throw new IllegalArgumentException("priority cannot be negative");
        }
    }
}
