package com.beyondsignal.game.combat.command;

import com.beyondsignal.game.combat.model.CombatId;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public abstract class CombatCommand {
    private final CombatId combatId;
    private final UUID actorId;
    private final Instant issuedAt;

    protected CombatCommand(CombatId combatId, UUID actorId, Instant issuedAt) {
        this.combatId = Objects.requireNonNull(combatId, "combatId");
        this.actorId = Objects.requireNonNull(actorId, "actorId");
        this.issuedAt = Objects.requireNonNull(issuedAt, "issuedAt");
    }

    public final CombatId combatId() { return combatId; }
    public final UUID actorId() { return actorId; }
    public final Instant issuedAt() { return issuedAt; }
}
