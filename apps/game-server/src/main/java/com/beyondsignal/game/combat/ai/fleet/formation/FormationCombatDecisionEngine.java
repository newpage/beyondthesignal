package com.beyondsignal.game.combat.ai.fleet.formation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class FormationCombatDecisionEngine {
    public FormationCombatDecision decide(
        FormationMemberCombatState member,
        FormationCombatPolicy policy,
        boolean commanderUnderThreat
    ) {
        Objects.requireNonNull(member, "member");
        Objects.requireNonNull(policy, "policy");

        Map<String, Integer> reasons = new LinkedHashMap<>();

        if (member.hullPercentage() <= policy.breakHullThreshold()
            || member.shieldPercentage() <= policy.breakShieldThreshold()) {
            reasons.put(
                "criticalHull",
                member.hullPercentage() <= policy.breakHullThreshold() ? 60 : 0
            );
            reasons.put(
                "criticalShields",
                member.shieldPercentage() <= policy.breakShieldThreshold() ? 40 : 0
            );

            return new FormationCombatDecision(
                member.participantId(),
                FormationCombatState.BROKEN_FOR_SURVIVAL,
                100,
                reasons
            );
        }

        if (policy.protectCommander()
            && commanderUnderThreat
            && !member.commander()) {
            reasons.put("commanderThreatened", 80);
            reasons.put("escortResponse", member.underDirectThreat() ? 10 : 20);

            return new FormationCombatDecision(
                member.participantId(),
                FormationCombatState.PROTECTING_COMMANDER,
                90,
                reasons
            );
        }

        if (member.state() == FormationCombatState.BROKEN_FOR_SURVIVAL
            && member.hullPercentage() >= policy.rejoinHullThreshold()
            && member.shieldPercentage() >= policy.rejoinShieldThreshold()) {
            reasons.put("hullRecovered", 40);
            reasons.put("shieldsRecovered", 40);

            return new FormationCombatDecision(
                member.participantId(),
                FormationCombatState.REJOINING,
                80,
                reasons
            );
        }

        reasons.put("formationOperational", 50);
        return new FormationCombatDecision(
            member.participantId(),
            FormationCombatState.MAINTAINING,
            50,
            reasons
        );
    }
}
