package com.beyondsignal.game.combat.ai.fleet.formation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FormationIntegrityCalculatorTest {
    @Test
    void calculatesStableFormation() {
        List<FormationMemberState> members = List.of(
            member(0.0, 0.0, 10.0),
            member(1.0, 0.0, 10.0),
            member(2.0, 0.0, 10.0)
        );

        FormationIntegrityCalculator calculator =
            new FormationIntegrityCalculator();
        FormationMetrics metrics = calculator.calculate(members);

        assertEquals(3, metrics.membersInPosition());
        assertEquals(FormationStatus.STABLE, calculator.status(metrics));
        assertTrue(metrics.integrity() >= 0.85);
    }

    @Test
    void identifiesBrokenFormation() {
        List<FormationMemberState> members = List.of(
            member(100.0, 0.0, 5.0),
            member(200.0, 0.0, 5.0)
        );

        FormationIntegrityCalculator calculator =
            new FormationIntegrityCalculator();
        FormationMetrics metrics = calculator.calculate(members);

        assertEquals(FormationStatus.BROKEN, calculator.status(metrics));
    }

    private static FormationMemberState member(
        double currentX,
        double desiredX,
        double tolerance
    ) {
        return new FormationMemberState(
            UUID.randomUUID(),
            new FormationVector(currentX, 0, 0),
            new FormationVector(desiredX, 0, 0),
            tolerance
        );
    }
}
