package com.beyondsignal.game.combat.ai.tactical;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class FocusFireCoordinator {
    public FocusFirePlan coordinate(
        List<AttackerProfile> attackers,
        List<TacticalTargetProfile> targets
    ) {
        Objects.requireNonNull(attackers, "attackers");
        Objects.requireNonNull(targets, "targets");

        TacticalTargetScorer scorer = new TacticalTargetScorer();
        List<TacticalTargetScore> rankedTargets = targets.stream()
            .map(scorer::score)
            .sorted()
            .toList();

        Map<UUID, TacticalTargetProfile> targetById = targets.stream()
            .collect(Collectors.toMap(
                TacticalTargetProfile::targetId,
                Function.identity()
            ));

        List<AttackerProfile> orderedAttackers = attackers.stream()
            .sorted(Comparator
                .comparing((AttackerProfile attacker) -> !attacker.commander())
                .thenComparing(attacker -> !attacker.escort())
                .thenComparing(
                    Comparator.comparingInt(AttackerProfile::availableDamage)
                        .reversed()
                )
                .thenComparing(AttackerProfile::attackerId))
            .toList();

        TargetReservationTable reservations = new TargetReservationTable();
        List<TargetAssignment> assignments = new ArrayList<>();

        for (AttackerProfile attacker : orderedAttackers) {
            TacticalTargetScore selected = selectTarget(
                attacker,
                rankedTargets,
                targetById,
                reservations
            );
            if (selected == null) {
                continue;
            }

            TargetAssignment assignment = new TargetAssignment(
                attacker.attackerId(),
                selected.targetId(),
                attacker.availableDamage(),
                selected.score()
            );
            assignments.add(assignment);
            reservations.reserve(new TargetReservation(
                attacker.attackerId(),
                selected.targetId(),
                attacker.availableDamage()
            ));
        }

        assignments.sort(Comparator.comparing(TargetAssignment::attackerId));
        return new FocusFirePlan(assignments, reservations);
    }

    private TacticalTargetScore selectTarget(
        AttackerProfile attacker,
        List<TacticalTargetScore> rankedTargets,
        Map<UUID, TacticalTargetProfile> targetById,
        TargetReservationTable reservations
    ) {
        for (TacticalTargetScore targetScore : rankedTargets) {
            TacticalTargetProfile profile = targetById.get(targetScore.targetId());
            int remainingDurability = remainingDurability(profile, reservations);
            if (remainingDurability <= 0) {
                continue;
            }

            if (attacker.escort() && profile.commander()) {
                return targetScore;
            }

            return targetScore;
        }

        return null;
    }

    private static int remainingDurability(
        TacticalTargetProfile target,
        TargetReservationTable reservations
    ) {
        int estimatedDurability = (int) Math.ceil(
            target.hullPercentage() * 100.0
                + target.shieldPercentage() * 100.0
        );
        return estimatedDurability - reservations.reservedDamage(target.targetId());
    }
}
