package com.beyondsignal.game.combat.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.beyondsignal.game.combat.command.NoOpCombatCommand;
import com.beyondsignal.game.combat.model.CombatId;
import com.beyondsignal.game.combat.model.CombatSide;
import com.beyondsignal.game.combat.rng.SplitMix64CombatRandom;
import com.beyondsignal.game.combat.shield.ShieldModel;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CombatEncounterTest {
    @Test
    void startsAndQueuesCommands() {
        CombatId combatId = CombatId.random();
        CombatEncounter encounter = encounter(combatId);
        CombatParticipant friendly = participant(CombatSide.FRIENDLY);
        CombatParticipant hostile = participant(CombatSide.HOSTILE);

        encounter.addParticipant(friendly);
        encounter.addParticipant(hostile);
        encounter.start();
        encounter.submit(new NoOpCombatCommand(
            combatId,
            friendly.participantId(),
            Instant.EPOCH
        ));

        assertEquals(1, encounter.queuedCommandCount());
        assertEquals(1, encounter.drainCommands().size());
    }

    @Test
    void requiresAtLeastTwoParticipants() {
        CombatEncounter encounter = encounter(CombatId.random());
        encounter.addParticipant(participant(CombatSide.FRIENDLY));
        assertThrows(IllegalStateException.class, encounter::start);
    }

    private static CombatEncounter encounter(CombatId id) {
        return new CombatEncounter(new CombatContext(
            id,
            1L,
            new SplitMix64CombatRandom(1L),
            new CombatClock()
        ));
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
