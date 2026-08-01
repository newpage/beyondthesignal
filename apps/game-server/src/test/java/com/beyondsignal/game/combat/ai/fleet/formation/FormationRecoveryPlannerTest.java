package com.beyondsignal.game.combat.ai.fleet.formation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FormationRecoveryPlannerTest {
    @Test
    void ordersLargestDriftFirst() {
        UUID close = UUID.fromString("00000000-0000-0000-0000-000000000011");
        UUID far = UUID.fromString("00000000-0000-0000-0000-000000000012");

        FormationRecoveryPlan plan = new FormationRecoveryPlanner().plan(List.of(
            member(close, 15.0, 5.0),
            member(far, 50.0, 5.0)
        ));

        assertTrue(plan.recoveryRequired());
        assertEquals(far, plan.actions().getFirst().participantId());
        assertTrue(plan.actions().getFirst().urgency()
            > plan.actions().getLast().urgency());
    }

    private static FormationMemberState member(
        UUID participant,
        double currentX,
        double tolerance
    ) {
        return new FormationMemberState(
            participant,
            new FormationVector(currentX, 0, 0),
            FormationVector.zero(),
            tolerance
        );
    }
}
