package com.beyondsignal.game.combat.weapon;

import java.util.Objects;
import java.util.UUID;

public record WeaponMountState(
    UUID mountId,
    WeaponDefinition definition,
    int chargeRemainingTicks,
    int cooldownRemainingTicks,
    int ammunitionRemaining,
    boolean enabled
) {
    public WeaponMountState {
        mountId = Objects.requireNonNull(mountId, "mountId");
        definition = Objects.requireNonNull(definition, "definition");
        if (chargeRemainingTicks < 0 || cooldownRemainingTicks < 0 || ammunitionRemaining < 0) {
            throw new IllegalArgumentException("Weapon state values cannot be negative");
        }
    }

    public static WeaponMountState ready(
        UUID mountId,
        WeaponDefinition definition,
        int ammunition
    ) {
        return new WeaponMountState(mountId, definition, 0, 0, ammunition, true);
    }

    public boolean readyToFire() {
        return enabled
            && chargeRemainingTicks == 0
            && cooldownRemainingTicks == 0
            && ammunitionRemaining >= definition.ammunitionPerShot();
    }

    public WeaponMountState beginCharge() {
        if (!enabled) {
            throw new IllegalStateException("Weapon mount is disabled");
        }
        if (cooldownRemainingTicks > 0) {
            throw new IllegalStateException("Weapon mount is cooling down");
        }
        return new WeaponMountState(
            mountId,
            definition,
            definition.chargeTicks(),
            cooldownRemainingTicks,
            ammunitionRemaining,
            enabled
        );
    }

    public WeaponMountState fire() {
        if (!readyToFire()) {
            throw new IllegalStateException("Weapon mount is not ready");
        }
        return new WeaponMountState(
            mountId,
            definition,
            0,
            definition.cooldownTicks(),
            ammunitionRemaining - definition.ammunitionPerShot(),
            enabled
        );
    }

    public WeaponMountState tick() {
        return new WeaponMountState(
            mountId,
            definition,
            Math.max(0, chargeRemainingTicks - 1),
            Math.max(0, cooldownRemainingTicks - 1),
            ammunitionRemaining,
            enabled
        );
    }

    public WeaponMountState disable() {
        return new WeaponMountState(
            mountId,
            definition,
            chargeRemainingTicks,
            cooldownRemainingTicks,
            ammunitionRemaining,
            false
        );
    }
}
