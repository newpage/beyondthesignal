package com.beyondsignal.game.combat.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import com.beyondsignal.game.combat.model.CombatId;
import com.beyondsignal.game.combat.model.CombatSide;
import com.beyondsignal.game.combat.shield.ShieldModel;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CombatSimulationEngineTest {
    @Test
    void createsStartsAndTicksEncounter() {
        CombatSimulationEngine engine = new CombatSimulationEngine();
        CombatId combatId = CombatId.random();
        CombatEncounter encounter = engine.createEncounter(combatId, 99L);

        encounter.addParticipant(participant(CombatSide.FRIENDLY));
        encounter.addParticipant(participant(CombatSide.HOSTILE));
        encounter.start();

        CombatTickResult result = engine.tick(combatId);

        assertEquals(1L, result.tick());
        assertEquals(CombatEncounterStatus.ACTIVE, result.encounterStatus());
        assertFalse(engine.completed(combatId));
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
