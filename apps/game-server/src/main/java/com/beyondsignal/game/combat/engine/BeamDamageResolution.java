package com.beyondsignal.game.combat.engine;

import com.beyondsignal.game.combat.shield.ShieldImpact;
import java.util.Objects;

public record BeamDamageResolution(
    ShieldImpact shieldImpact,
    int hullDamage,
    int hullRemaining,
    boolean shieldCollapsedNow,
    boolean targetDestroyed
) {
    public BeamDamageResolution {
        shieldImpact = Objects.requireNonNull(shieldImpact, "shieldImpact");
        if (hullDamage < 0 || hullRemaining < 0) {
            throw new IllegalArgumentException("Hull values cannot be negative");
        }
    }
}
