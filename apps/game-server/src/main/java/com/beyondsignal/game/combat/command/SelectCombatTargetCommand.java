package com.beyondsignal.game.combat.command;

import com.beyondsignal.game.combat.model.CombatId;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class SelectCombatTargetCommand extends CombatCommand {
    private final UUID targetId;

    public SelectCombatTargetCommand(
        CombatId combatId,
        UUID actorId,
        UUID targetId,
        Instant issuedAt
    ) {
        super(combatId, actorId, issuedAt);
        this.targetId = Objects.requireNonNull(targetId, "targetId");
    }

    public UUID targetId() {
        return targetId;
    }
}
