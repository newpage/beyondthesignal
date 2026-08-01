package com.beyondsignal.game.combat.ai.fleet.formation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FormationObjectiveSelectorTest {
    @Test
    void selectsRecoveryForDegradedFormation() {
        UUID fleetId = UUID.randomUUID();
        FormationState state = new FormationState(
            fleetId,
            FormationType.WEDGE,
            FormationStatus.DEGRADED,
            List.of(),
            List.of(),
            0.35
        );

        FormationObjective objective =
            new FormationObjectiveSelector().select(fleetId, state);

        assertEquals(
            FormationObjectiveType.RECOVER_FORMATION,
            objective.type()
        );
        assertEquals(80, objective.priority());
    }

    @Test
    void selectsMaintenanceForStableFormation() {
        UUID fleetId = UUID.randomUUID();
        FormationState state = new FormationState(
            fleetId,
            FormationType.DIAMOND,
            FormationStatus.STABLE,
            List.of(),
            List.of(),
            0.95
        );

        FormationObjective objective =
            new FormationObjectiveSelector().select(fleetId, state);

        assertEquals(
            FormationObjectiveType.MAINTAIN_FORMATION,
            objective.type()
        );
    }
}
