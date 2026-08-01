package com.beyondsignal.game.combat.ai.tactical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TacticalAssignmentServiceIntegrationTest {
    @Test
    void coordinatesTenAttackersAcrossMultipleTargets() {
        List<AttackerProfile> attackers = new ArrayList<>();
        for (int index = 0; index < 10; index++) {
            attackers.add(new AttackerProfile(
                UUID.nameUUIDFromBytes(("attacker-" + index).getBytes()),
                25 + index,
                index < 3,
                index == 0
            ));
        }

        List<TacticalTargetProfile> targets = List.of(
            target("flagship", TargetPriorityClass.FLAGSHIP, 1.0, 1.0, 40, true, true),
            target("support", TargetPriorityClass.SUPPORT, 0.8, 0.6, 10, false, false),
            target("escort", TargetPriorityClass.ESCORT, 1.0, 1.0, 20, false, false)
        );

        FocusFirePlan first = new TacticalAssignmentService().assignTargets(
            attackers,
            targets
        );
        FocusFirePlan second = new TacticalAssignmentService().assignTargets(
            attackers,
            targets
        );

        assertEquals(first.assignments(), second.assignments());
        assertEquals(10, first.assignments().size());
    }

    private static TacticalTargetProfile target(
        String name,
        TargetPriorityClass priorityClass,
        double hull,
        double shields,
        int damage,
        boolean targetingFleet,
        boolean commander
    ) {
        return new TacticalTargetProfile(
            UUID.nameUUIDFromBytes(name.getBytes()),
            priorityClass,
            hull,
            shields,
            damage,
            targetingFleet,
            commander,
            priorityClass == TargetPriorityClass.SUPPORT
        );
    }
}
