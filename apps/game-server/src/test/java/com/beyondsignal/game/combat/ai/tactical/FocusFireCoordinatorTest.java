package com.beyondsignal.game.combat.ai.tactical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FocusFireCoordinatorTest {
    @Test
    void avoidsAssigningExcessDamageToDestroyedTarget() {
        UUID attackerOne = UUID.randomUUID();
        UUID attackerTwo = UUID.randomUUID();
        UUID weakTarget = UUID.randomUUID();
        UUID strongTarget = UUID.randomUUID();

        FocusFirePlan plan = new FocusFireCoordinator().coordinate(
            List.of(
                new AttackerProfile(attackerOne, 120, false, false),
                new AttackerProfile(attackerTwo, 120, false, false)
            ),
            List.of(
                new TacticalTargetProfile(
                    weakTarget,
                    TargetPriorityClass.CAPITAL_SHIP,
                    0.20,
                    0.10,
                    30,
                    true,
                    false,
                    false
                ),
                new TacticalTargetProfile(
                    strongTarget,
                    TargetPriorityClass.ESCORT,
                    1.0,
                    1.0,
                    10,
                    false,
                    false,
                    false
                )
            )
        );

        assertEquals(2, plan.assignments().size());
        assertTrue(plan.assignments().stream()
            .anyMatch(assignment -> assignment.targetId().equals(weakTarget)));
        assertTrue(plan.assignments().stream()
            .anyMatch(assignment -> assignment.targetId().equals(strongTarget)));
    }

    @Test
    void producesDeterministicAssignments() {
        List<AttackerProfile> attackers = List.of(
            new AttackerProfile(
                UUID.fromString("00000000-0000-0000-0000-000000000101"),
                40,
                false,
                true
            ),
            new AttackerProfile(
                UUID.fromString("00000000-0000-0000-0000-000000000102"),
                30,
                true,
                false
            )
        );

        List<TacticalTargetProfile> targets = List.of(
            new TacticalTargetProfile(
                UUID.fromString("00000000-0000-0000-0000-000000000201"),
                TargetPriorityClass.FLAGSHIP,
                1.0,
                1.0,
                40,
                true,
                true,
                false
            ),
            new TacticalTargetProfile(
                UUID.fromString("00000000-0000-0000-0000-000000000202"),
                TargetPriorityClass.SUPPORT,
                1.0,
                1.0,
                10,
                false,
                false,
                true
            )
        );

        FocusFireCoordinator coordinator = new FocusFireCoordinator();

        assertEquals(
            coordinator.coordinate(attackers, targets).assignments(),
            coordinator.coordinate(attackers, targets).assignments()
        );
    }
}
