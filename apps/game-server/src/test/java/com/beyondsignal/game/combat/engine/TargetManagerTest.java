package com.beyondsignal.game.combat.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.beyondsignal.game.combat.model.CombatId;
import com.beyondsignal.game.combat.model.CombatSide;
import com.beyondsignal.game.combat.rng.SplitMix64CombatRandom;
import com.beyondsignal.game.combat.shield.ShieldModel;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TargetManagerTest {
    @Test
    void selectsHostileTargetAndRejectsFriendly() {
        CombatEncounter encounter = encounter();
        CombatParticipant friendly = participant(CombatSide.FRIENDLY);
        CombatParticipant wingman = participant(CombatSide.FRIENDLY);
        CombatParticipant hostile = participant(CombatSide.HOSTILE);
        encounter.addParticipant(friendly);
        encounter.addParticipant(wingman);
        encounter.addParticipant(hostile);
        encounter.start();

        TargetManager manager = new TargetManager();
        manager.selectTarget(encounter, friendly, hostile.participantId());

        assertEquals(hostile.participantId(), friendly.selectedTargetId().orElseThrow());
        assertThrows(
            IllegalArgumentException.class,
            () -> manager.selectTarget(encounter, friendly, wingman.participantId())
        );
    }

    private static CombatEncounter encounter() {
        CombatId id = CombatId.random();
        return new CombatEncounter(new CombatContext(
            id, 1L, new SplitMix64CombatRandom(1L), new CombatClock()
        ));
    }

    private static CombatParticipant participant(CombatSide side) {
        return new CombatParticipant(
            UUID.randomUUID(), side, ShieldModel.uniform(50, 1),
            List.of(), 100, 0.8, 0.2
        );
    }
}
