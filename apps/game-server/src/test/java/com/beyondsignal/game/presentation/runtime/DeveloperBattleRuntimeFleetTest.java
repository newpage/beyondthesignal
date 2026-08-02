package com.beyondsignal.game.presentation.runtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.beyondsignal.game.combat.engine.CombatSimulationEngine;
import com.beyondsignal.game.combat.fleet.FleetDoctrine;
import com.beyondsignal.game.combat.fleet.FleetOrderType;
import com.beyondsignal.game.combat.model.CombatId;
import io.vertx.core.Vertx;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DeveloperBattleRuntimeFleetTest {
    @Test
    void registersDeterministicDeveloperFleetsBeforeCombatStarts() {
        long seed = 701L;
        Vertx vertx = Vertx.vertx();
        CombatSimulationEngine engine = new CombatSimulationEngine();

        try (DeveloperBattleRuntime ignored =
                 new DeveloperBattleRuntime(vertx, engine, seed)) {
            CombatId combatId = new CombatId(UUID.nameUUIDFromBytes(
                ("developer-battle-" + seed).getBytes()
            ));
            var encounter = engine.encounter(combatId).orElseThrow();

            assertEquals(2, encounter.fleets().size());

            var alliance = encounter.fleets().stream()
                .filter(fleet -> fleet.name().equals("Alliance Task Force"))
                .findFirst()
                .orElseThrow();
            assertEquals(FleetDoctrine.DEFENSIVE, alliance.doctrine());
            assertEquals(3, alliance.squadrons().getFirst().memberIds().size());
            assertEquals(
                FleetOrderType.DEFEND,
                alliance.orders().getFirst().type()
            );

            var hostile = encounter.fleets().stream()
                .filter(fleet -> fleet.name().equals("Hostile Strike Force"))
                .findFirst()
                .orElseThrow();
            assertEquals(FleetDoctrine.AGGRESSIVE, hostile.doctrine());
            assertEquals(2, hostile.squadrons().getFirst().memberIds().size());
            assertEquals(
                FleetOrderType.ATTACK,
                hostile.orders().getFirst().type()
            );

            assertTrue(encounter.status().name().equals("ACTIVE"));
        } finally {
            vertx.close();
        }
    }
}
