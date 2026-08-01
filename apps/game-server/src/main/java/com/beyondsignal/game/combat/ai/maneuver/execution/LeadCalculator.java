package com.beyondsignal.game.combat.ai.maneuver.execution;

import com.beyondsignal.game.combat.ai.maneuver.geometry.CombatVector;
import com.beyondsignal.game.combat.ai.maneuver.geometry.InterceptionCalculator;
import com.beyondsignal.game.combat.ai.maneuver.geometry.InterceptionSolution;
import java.util.Objects;

public final class LeadCalculator {
    private final InterceptionCalculator interceptionCalculator;

    public LeadCalculator() {
        this(new InterceptionCalculator());
    }

    public LeadCalculator(InterceptionCalculator interceptionCalculator) {
        this.interceptionCalculator = Objects.requireNonNull(
            interceptionCalculator,
            "interceptionCalculator"
        );
    }

    public LeadSolution calculate(
        CombatVector shooterPosition,
        double projectileSpeed,
        CombatVector targetPosition,
        CombatVector targetVelocity
    ) {
        InterceptionSolution interception = interceptionCalculator.calculate(
            shooterPosition,
            projectileSpeed,
            targetPosition,
            targetVelocity
        );

        return new LeadSolution(
            interception.interceptPoint(),
            interception.interceptVector(),
            interception.timeToIntercept(),
            interception.feasible()
        );
    }
}
