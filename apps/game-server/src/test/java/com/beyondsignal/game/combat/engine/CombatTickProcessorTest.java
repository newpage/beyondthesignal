package com.beyondsignal.game.combat.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.beyondsignal.game.combat.command.NoOpCombatCommand;
import com.beyondsignal.game.combat.model.CombatId;
import com.beyondsignal.game.combat.model.CombatSide;
import com.beyondsignal.game.combat.rng.SplitMix64CombatRandom;
import com.beyondsignal.game.combat.shield.ShieldModel;
import com.beyondsignal.game.combat.weapon.DamageType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CombatTickProcessorTest {
    @Test
    void advancesClockRegeneratesShieldsAndDrainsCommands() {
        CombatId combatId = CombatId.random();
        CombatEncounter encounter = new CombatEncounter(new CombatContext(
            combatId,
            5L,
            new SplitMix64CombatRandom(5L),
            new CombatClock()
        ));

        CombatParticipant friendly = participant(CombatSide.FRIENDLY);
        CombatParticipant hostile = participant(CombatSide.HOSTILE);
        friendly.shields().apply(
            com.beyondsignal.game.combat.shield.ShieldQuadrant.FORWARD,
            DamageType.ENERGY,
            10,
            1.0
        );

        encounter.addParticipant(friendly);
        encounter.addParticipant(hostile);
        encounter.start();
        encounter.submit(new NoOpCombatCommand(
            combatId,
            friendly.participantId(),
            Instant.EPOCH
        ));

        CombatTickResult result = new CombatTickProcessor().process(encounter);

        assertEquals(1L, result.tick());
        assertEquals(1, result.commandsProcessed());
        assertEquals(41, friendly.shields()
            .state(com.beyondsignal.game.combat.shield.ShieldQuadrant.FORWARD)
            .strength());
        assertEquals(0, encounter.queuedCommandCount());
    }

    private static CombatParticipant participant(CombatSide side) {
        return new CombatParticipant(
            UUID.randomUUID(),
            side,
            ShieldModel.uniform(50, 1),
            List.of(),
            100,
            0.8,
            0.2
        );
    }
}
