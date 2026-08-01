package com.beyondsignal.game.combat.ai.fleet.formation;

import com.beyondsignal.game.combat.ai.fleet.FleetDefinition;
import java.util.Objects;

public final class FormationPlanner {
    private final FormationAllocator allocator;
    private final FormationValidator validator;

    public FormationPlanner() {
        this(new FormationAllocator(), new FormationValidator());
    }

    public FormationPlanner(
        FormationAllocator allocator,
        FormationValidator validator
    ) {
        this.allocator = Objects.requireNonNull(allocator, "allocator");
        this.validator = Objects.requireNonNull(validator, "validator");
    }

    public FormationPlan plan(
        FleetDefinition fleet,
        FormationTemplate template,
        FormationAnchor anchor
    ) {
        Objects.requireNonNull(fleet, "fleet");
        Objects.requireNonNull(template, "template");
        Objects.requireNonNull(anchor, "anchor");

        var assignments = allocator.allocate(fleet.members(), template, anchor);
        validator.validateAssignments(template, assignments);

        return new FormationPlan(
            fleet.fleetId(),
            template,
            anchor,
            assignments
        );
    }
}
