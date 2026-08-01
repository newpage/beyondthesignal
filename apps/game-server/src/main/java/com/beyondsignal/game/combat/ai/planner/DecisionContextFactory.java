package com.beyondsignal.game.combat.ai.planner;

import com.beyondsignal.game.combat.ai.evaluation.ThreatAnalyzer;
import com.beyondsignal.game.combat.ai.sensor.CombatSensorSuite;
import com.beyondsignal.game.combat.ai.snapshot.CombatAiSnapshot;
import java.util.Objects;
import java.util.UUID;

public final class DecisionContextFactory {
    private final CombatSensorSuite sensors;
    private final ThreatAnalyzer analyzer;

    public DecisionContextFactory() {
        this(new CombatSensorSuite(), new ThreatAnalyzer());
    }

    public DecisionContextFactory(
        CombatSensorSuite sensors,
        ThreatAnalyzer analyzer
    ) {
        this.sensors = Objects.requireNonNull(sensors, "sensors");
        this.analyzer = Objects.requireNonNull(analyzer, "analyzer");
    }

    public DecisionContext create(CombatAiSnapshot snapshot, UUID selfId) {
        return new DecisionContext(
            snapshot,
            sensors.self(snapshot, selfId),
            analyzer.analyze(snapshot, selfId)
        );
    }
}
