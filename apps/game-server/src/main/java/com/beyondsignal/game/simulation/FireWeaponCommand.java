package com.beyondsignal.game.simulation;

import java.util.Locale;
import java.util.Objects;

public record FireWeaponCommand(String weapon) implements ShipCommand {
    public FireWeaponCommand {
        Objects.requireNonNull(weapon, "weapon must not be null");
        weapon = weapon.trim().toUpperCase(Locale.ROOT);
        if (!"PHASER".equals(weapon)) {
            throw new IllegalArgumentException("weapon must be PHASER");
        }
    }
}
