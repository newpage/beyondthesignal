package com.beyondsignal.game.combat.ai.fleet;

import com.beyondsignal.game.combat.ai.evaluation.ThreatAnalyzer;
import com.beyondsignal.game.combat.ai.snapshot.CombatantSnapshot;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class FleetThreatAnalyzer {
    private final ThreatAnalyzer threatAnalyzer;

    public FleetThreatAnalyzer() {
        this(new ThreatAnalyzer());
    }

    public FleetThreatAnalyzer(ThreatAnalyzer threatAnalyzer) {
        this.threatAnalyzer = Objects.requireNonNull(threatAnalyzer, "threatAnalyzer");
    }

    public FleetThreatAssessment analyze(FleetSnapshot snapshot) {
        Objects.requireNonNull(snapshot, "snapshot");
        Map<java.util.UUID, Integer> totals = new LinkedHashMap<>();

        for (CombatantSnapshot observer : snapshot.activeMembers()) {
            threatAnalyzer.analyze(snapshot.combat(), observer.participantId())
                .threats()
                .forEach(score -> totals.merge(
                    score.targetId(),
                    score.total(),
                    Integer::sum
                ));
        }

        return new FleetThreatAssessment(totals);
    }
}
