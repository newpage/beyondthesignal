package com.beyondsignal.game.combat.ai.fleet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.beyondsignal.game.combat.ai.snapshot.CombatAiSnapshot;
import com.beyondsignal.game.combat.ai.snapshot.CombatantSnapshot;
import com.beyondsignal.game.combat.ai.snapshot.ShieldSnapshot;
import com.beyondsignal.game.combat.engine.CombatParticipantStatus;
import com.beyondsignal.game.combat.model.CombatId;
import com.beyondsignal.game.combat.model.CombatSide;
import com.beyondsignal.game.combat.shield.ShieldQuadrant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FleetObjectiveSelectorTest {
    @Test
    void retreatsFleetWhenAverageHullIsCritical() {
        UUID member = UUID.randomUUID();
        FleetSnapshot snapshot = new FleetSnapshot(
            UUID.randomUUID(),
            FleetDoctrine.DEFENSIVE_SCREEN,
            List.of(new FleetMember(member, FleetRole.COMMANDER, 100)),
            new CombatAiSnapshot(
                CombatId.random(),
                1L,
                List.of(combatant(member, 20))
            )
        );

        FleetObjective objective = new FleetObjectiveSelector().select(
            snapshot,
            new FleetThreatAssessment(Map.of())
        );

        assertEquals(FleetObjectiveType.RETREAT_FLEET, objective.type());
    }

    private static CombatantSnapshot combatant(UUID id, int hull) {
        EnumMap<ShieldQuadrant, Integer> values = new EnumMap<>(ShieldQuadrant.class);
        for (ShieldQuadrant quadrant : ShieldQuadrant.values()) {
            values.put(quadrant, 50);
        }
        return new CombatantSnapshot(
            id,
            CombatSide.FRIENDLY,
            CombatParticipantStatus.ACTIVE,
            hull,
            100,
            new ShieldSnapshot(values, values),
            List.of(),
            null,
            0.8,
            0.2
        );
    }
}
