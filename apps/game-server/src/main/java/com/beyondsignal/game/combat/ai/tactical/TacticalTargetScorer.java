package com.beyondsignal.game.combat.ai.tactical;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class TacticalTargetScorer {
    public TacticalTargetScore score(TacticalTargetProfile target) {
        Objects.requireNonNull(target, "target");

        Map<String, Integer> factors = new LinkedHashMap<>();
        factors.put("priorityClass", classScore(target.priorityClass()));
        factors.put("weaponThreat", Math.min(35, target.readyWeaponDamage()));
        factors.put(
            "vulnerability",
            (int) Math.round((1.0 - target.hullPercentage()) * 20.0)
        );
        factors.put(
            "shieldWeakness",
            (int) Math.round((1.0 - target.shieldPercentage()) * 15.0)
        );
        factors.put("targetingFleetMember", target.targetingFleetMember() ? 20 : 0);
        factors.put("commander", target.commander() ? 25 : 0);
        factors.put("supportAsset", target.supportAsset() ? 15 : 0);

        int total = factors.values().stream().mapToInt(Integer::intValue).sum();
        return new TacticalTargetScore(target.targetId(), total, factors);
    }

    private static int classScore(TargetPriorityClass priorityClass) {
        return switch (priorityClass) {
            case FLAGSHIP -> 40;
            case CAPITAL_SHIP -> 30;
            case SUPPORT -> 25;
            case STRIKE -> 20;
            case ESCORT -> 15;
            case SCOUT -> 10;
            case UNKNOWN -> 5;
        };
    }
}
