package com.beyondsignal.game.combat.ai.fleet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.beyondsignal.game.combat.ai.snapshot.CombatAiSnapshot;
import com.beyondsignal.game.combat.ai.snapshot.CombatantSnapshot;
import com.beyondsignal.game.combat.ai.snapshot.ShieldSnapshot;
import com.beyondsignal.game.combat.ai.snapshot.WeaponSnapshot;
import com.beyondsignal.game.combat.engine.CombatParticipantStatus;
import com.beyondsignal.game.combat.model.CombatId;
import com.beyondsignal.game.combat.model.CombatSide;
import com.beyondsignal.game.combat.shield.ShieldQuadrant;
import com.beyondsignal.game.combat.weapon.WeaponType;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FleetThreatAnalyzerTest {
    @Test
    void aggregatesThreatAcrossFleetObservers() {
        UUID fleetId = UUID.randomUUID();
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        UUID hostile = UUID.randomUUID();

        FleetSnapshot snapshot = new FleetSnapshot(
            fleetId,
            FleetDoctrine.FOCUS_FIRE,
            List.of(
                new FleetMember(first, FleetRole.COMMANDER, 100),
                new FleetMember(second, FleetRole.ESCORT, 50)
            ),
            new CombatAiSnapshot(
                CombatId.random(),
                1L,
                List.of(
                    combatant(first, CombatSide.FRIENDLY, null, List.of()),
                    combatant(second, CombatSide.FRIENDLY, null, List.of()),
                    combatant(hostile, CombatSide.HOSTILE, first, List.of(
                        weapon(30)
                    ))
                )
            )
        );

        FleetThreatAssessment result = new FleetThreatAnalyzer().analyze(snapshot);

        assertEquals(hostile, result.highestThreatTarget().orElseThrow());
        assertEquals(2, result.threatByTarget().size() == 1 ? 2 : 0);
    }

    private static CombatantSnapshot combatant(
        UUID id,
        CombatSide side,
        UUID targetId,
        List<WeaponSnapshot> weapons
    ) {
        EnumMap<ShieldQuadrant, Integer> values = new EnumMap<>(ShieldQuadrant.class);
        for (ShieldQuadrant quadrant : ShieldQuadrant.values()) {
            values.put(quadrant, 50);
        }
        return new CombatantSnapshot(
            id,
            side,
            CombatParticipantStatus.ACTIVE,
            100,
            100,
            new ShieldSnapshot(values, values),
            weapons,
            targetId,
            0.8,
            0.2
        );
    }

    private static WeaponSnapshot weapon(int damage) {
        return new WeaponSnapshot(
            UUID.randomUUID(),
            "weapon-" + damage,
            WeaponType.BEAM,
            true,
            0,
            0,
            damage,
            10000
        );
    }
}
