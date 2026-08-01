package com.beyondsignal.game.combat.ai.fleet;

import java.util.Objects;

public final class FleetCommander {
    private final FleetThreatAnalyzer threatAnalyzer;
    private final FleetObjectiveSelector objectiveSelector;
    private final FleetPlanner planner;

    public FleetCommander() {
        this(
            new FleetThreatAnalyzer(),
            new FleetObjectiveSelector(),
            new FleetPlanner()
        );
    }

    public FleetCommander(
        FleetThreatAnalyzer threatAnalyzer,
        FleetObjectiveSelector objectiveSelector,
        FleetPlanner planner
    ) {
        this.threatAnalyzer = Objects.requireNonNull(threatAnalyzer, "threatAnalyzer");
        this.objectiveSelector = Objects.requireNonNull(
            objectiveSelector,
            "objectiveSelector"
        );
        this.planner = Objects.requireNonNull(planner, "planner");
    }

    public FleetPlan decide(FleetSnapshot snapshot) {
        FleetThreatAssessment threats = threatAnalyzer.analyze(snapshot);
        FleetObjective objective = objectiveSelector.select(snapshot, threats);
        return planner.plan(snapshot, objective);
    }
}
