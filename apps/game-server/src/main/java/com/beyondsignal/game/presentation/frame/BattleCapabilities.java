package com.beyondsignal.game.presentation.frame;

import java.util.Set;

public record BattleCapabilities(Set<BattleCapability> enabled) {
    public BattleCapabilities {
        enabled = Set.copyOf(enabled);
    }

    public boolean supports(BattleCapability capability) {
        return enabled.contains(capability);
    }

    public static BattleCapabilities core() {
        return new BattleCapabilities(Set.of(
            BattleCapability.SHIPS,
            BattleCapability.MOVEMENT,
            BattleCapability.PROJECTILES,
            BattleCapability.DEBUG
        ));
    }
}
