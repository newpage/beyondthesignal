package com.beyondsignal.game.combat.ai.fleet.formation;

import com.beyondsignal.game.combat.ai.fleet.FleetDefinition;
import java.util.Objects;

public final class FormationCasualtyProcessor {
    private final FormationReassignmentPlanner reassignmentPlanner;

    public FormationCasualtyProcessor() {
        this(new FormationReassignmentPlanner());
    }

    public FormationCasualtyProcessor(
        FormationReassignmentPlanner reassignmentPlanner
    ) {
        this.reassignmentPlanner = Objects.requireNonNull(
            reassignmentPlanner,
            "reassignmentPlanner"
        );
    }

    public FormationPlan process(
        FleetDefinition fleet,
        FormationCasualtyUpdate casualties,
        FormationTemplate template,
        FormationAnchor anchor
    ) {
        Objects.requireNonNull(fleet, "fleet");
        Objects.requireNonNull(casualties, "casualties");
        Objects.requireNonNull(template, "template");
        Objects.requireNonNull(anchor, "anchor");

        if (!casualties.hasLosses()) {
            return new FormationPlanner().plan(fleet, template, anchor);
        }

        return reassignmentPlanner.reassignAfterLoss(
            fleet,
            casualties.unavailableParticipantIds(),
            template,
            anchor
        );
    }
}
