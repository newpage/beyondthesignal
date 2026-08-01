package com.beyondsignal.game.combat.ai.maneuver.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.beyondsignal.game.combat.ai.maneuver.geometry.CombatVector;
import org.junit.jupiter.api.Test;

class OrbitAndKitingIntegrationTest {
    @Test
    void producesDeterministicPlansForFiftyShips() {
        OrbitPlanner orbitPlanner = new OrbitPlanner();
        KitingPlanner kitingPlanner = new KitingPlanner();

        List<OrbitPlan> firstOrbit = new ArrayList<>();
        List<OrbitPlan> secondOrbit = new ArrayList<>();
        List<KitingPlan> firstKite = new ArrayList<>();
        List<KitingPlan> secondKite = new ArrayList<>();

        for (int index = 0; index < 50; index++) {
            UUID participant = UUID.nameUUIDFromBytes(
                ("participant-" + index).getBytes()
            );
            UUID target = UUID.nameUUIDFromBytes(
                ("target-" + index).getBytes()
            );

            OrbitContext orbitContext = new OrbitContext(
                participant,
                target,
                new CombatVector(400 + index * 5.0, 0, index),
                new CombatVector(0, 0, 50),
                CombatVector.zero(),
                new CombatVector(0, 1, 0),
                500.0,
                25.0,
                50.0
            );

            KitingContext kitingContext = new KitingContext(
                participant,
                target,
                new CombatVector(index * 10.0, 0, 0),
                new CombatVector(400 + index * 5.0, 0, 0),
                new CombatVector(5, 0, 0),
                500.0,
                25.0,
                100.0
            );

            firstOrbit.add(
                orbitPlanner.plan(
                    orbitContext,
                    index % 2 == 0
                        ? OrbitDirection.CLOCKWISE
                        : OrbitDirection.COUNTERCLOCKWISE
                )
            );
            secondOrbit.add(
                orbitPlanner.plan(
                    orbitContext,
                    index % 2 == 0
                        ? OrbitDirection.CLOCKWISE
                        : OrbitDirection.COUNTERCLOCKWISE
                )
            );
            firstKite.add(kitingPlanner.plan(kitingContext));
            secondKite.add(kitingPlanner.plan(kitingContext));
        }

        assertEquals(firstOrbit, secondOrbit);
        assertEquals(firstKite, secondKite);
        assertEquals(50, firstOrbit.size());
        assertEquals(50, firstKite.size());
    }
}
