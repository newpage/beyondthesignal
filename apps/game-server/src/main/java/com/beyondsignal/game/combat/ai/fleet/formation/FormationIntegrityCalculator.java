package com.beyondsignal.game.combat.ai.fleet.formation;

import java.util.List;
import java.util.Objects;

public final class FormationIntegrityCalculator {
    public FormationMetrics calculate(List<FormationMemberState> members) {
        Objects.requireNonNull(members, "members");

        if (members.isEmpty()) {
            return new FormationMetrics(0, 0, 0.0, 0.0, 0.0);
        }

        int inPosition = 0;
        double totalDrift = 0.0;
        double maximumDrift = 0.0;

        for (FormationMemberState member : members) {
            double drift = member.driftDistance();
            totalDrift += drift;
            maximumDrift = Math.max(maximumDrift, drift);
            if (member.inPosition()) {
                inPosition++;
            }
        }

        double averageDrift = totalDrift / members.size();
        double positionRatio = inPosition / (double) members.size();

        double normalizedDriftPenalty = members.stream()
            .mapToDouble(member -> {
                double tolerance = Math.max(member.tolerance(), 1.0);
                return Math.min(1.0, member.driftDistance() / (tolerance * 4.0));
            })
            .average()
            .orElse(1.0);

        double integrity = clamp(positionRatio * 0.7
            + (1.0 - normalizedDriftPenalty) * 0.3);

        return new FormationMetrics(
            members.size(),
            inPosition,
            averageDrift,
            maximumDrift,
            integrity
        );
    }

    public FormationStatus status(FormationMetrics metrics) {
        Objects.requireNonNull(metrics, "metrics");

        if (metrics.assignedMembers() == 0) {
            return FormationStatus.BROKEN;
        }
        if (metrics.integrity() >= 0.85) {
            return FormationStatus.STABLE;
        }
        if (metrics.integrity() >= 0.50) {
            return FormationStatus.FORMING;
        }
        if (metrics.integrity() >= 0.20) {
            return FormationStatus.DEGRADED;
        }
        return FormationStatus.BROKEN;
    }

    private static double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}
