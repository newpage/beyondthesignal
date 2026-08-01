package com.beyondsignal.game.combat.ai.fleet;

import com.beyondsignal.game.combat.ai.snapshot.CombatantSnapshot;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class FleetObjectiveSelector {
    public FleetObjective select(
        FleetSnapshot snapshot,
        FleetThreatAssessment threats
    ) {
        Objects.requireNonNull(snapshot, "snapshot");
        Objects.requireNonNull(threats, "threats");

        double averageHull = snapshot.activeMembers().stream()
            .mapToDouble(CombatantSnapshot::hullPercentage)
            .average()
            .orElse(0.0);

        if (averageHull <= 0.25) {
            return new FleetObjective(
                FleetObjectiveType.RETREAT_FLEET,
                null,
                100,
                Map.of("averageHullCritical", 100)
            );
        }

        UUID target = threats.highestThreatTarget().orElse(null);
        if (target == null) {
            return new FleetObjective(
                FleetObjectiveType.HOLD_FORMATION,
                null,
                10,
                Map.of("noHostiles", 10)
            );
        }

        int threat = threats.threatByTarget().getOrDefault(target, 0);
        int doctrineBonus = switch (snapshot.doctrine()) {
            case FOCUS_FIRE -> 35;
            case AGGRESSIVE_ADVANCE -> 25;
            case BALANCED -> 15;
            case DEFENSIVE_SCREEN -> 5;
        };

        Map<String, Integer> reasons = new LinkedHashMap<>();
        reasons.put("fleetThreat", threat);
        reasons.put("doctrineBonus", doctrineBonus);

        return new FleetObjective(
            FleetObjectiveType.FOCUS_FIRE,
            target,
            threat + doctrineBonus,
            reasons
        );
    }
}
