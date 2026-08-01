package com.beyondsignal.game.combat.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public record CombatId(UUID value) implements Serializable {
    public CombatId {
        Objects.requireNonNull(value, "value");
    }

    public static CombatId random() {
        return new CombatId(UUID.randomUUID());
    }

    public static CombatId fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("value is required");
        }
        return new CombatId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
