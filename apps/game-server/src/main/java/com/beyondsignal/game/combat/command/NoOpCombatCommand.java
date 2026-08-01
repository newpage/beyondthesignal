package com.beyondsignal.game.combat.command;

import com.beyondsignal.game.combat.model.CombatId;
import java.time.Instant;
import java.util.UUID;

/**
 * Test and synchronization command that intentionally performs no combat action.
 */
public final class NoOpCombatCommand extends CombatCommand {
    public NoOpCombatCommand(CombatId combatId, UUID actorId, Instant issuedAt) {
        super(combatId, actorId, issuedAt);
    }
}
