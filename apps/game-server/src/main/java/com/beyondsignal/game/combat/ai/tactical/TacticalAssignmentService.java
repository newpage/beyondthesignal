package com.beyondsignal.game.combat.ai.tactical;

import java.util.List;
import java.util.Objects;

public final class TacticalAssignmentService {
    private final FocusFireCoordinator focusFireCoordinator;

    public TacticalAssignmentService() {
        this(new FocusFireCoordinator());
    }

    public TacticalAssignmentService(
        FocusFireCoordinator focusFireCoordinator
    ) {
        this.focusFireCoordinator = Objects.requireNonNull(
            focusFireCoordinator,
            "focusFireCoordinator"
        );
    }

    public FocusFirePlan assignTargets(
        List<AttackerProfile> attackers,
        List<TacticalTargetProfile> targets
    ) {
        return focusFireCoordinator.coordinate(attackers, targets);
    }
}
