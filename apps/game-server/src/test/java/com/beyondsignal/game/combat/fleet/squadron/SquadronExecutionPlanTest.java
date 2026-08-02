package com.beyondsignal.game.combat.fleet.squadron;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class SquadronExecutionPlanTest {
    @Test
    void copiesMemberAssignments() {
        var members = new ArrayList<UUID>();
        members.add(UUID.randomUUID());

        var plan = new SquadronExecutionPlan(
            UUID.randomUUID(),
            UUID.randomUUID(),
            SquadronExecutionMode.FOCUS_FIRE,
            UUID.randomUUID(),
            members,
            1.0,
            4
        );
        members.clear();

        assertEquals(1, plan.memberIds().size());
    }
}
