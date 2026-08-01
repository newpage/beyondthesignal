package com.beyondsignal.game.combat.ai.fleet.formation;

import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FormationValidatorTest {
    @Test
    void rejectsDuplicateParticipantAssignments() {
        FormationTemplate template =
            FormationTemplates.create(FormationType.DIAMOND, 500.0);
        UUID participant = UUID.randomUUID();
        FormationAnchor anchor = new FormationAnchor(
            participant,
            FormationVector.zero(),
            new FormationVector(0, 0, 1),
            new FormationVector(1, 0, 0),
            new FormationVector(0, 1, 0)
        );

        FormationAssignment first = new FormationAssignment(
            participant,
            template.slot(0),
            anchor.worldPosition(template.slot(0), template.spacing())
        );
        FormationAssignment second = new FormationAssignment(
            participant,
            template.slot(1),
            anchor.worldPosition(template.slot(1), template.spacing())
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> new FormationValidator().validateAssignments(
                template,
                List.of(first, second)
            )
        );
    }
}
