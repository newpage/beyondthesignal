package com.beyondsignal.game.combat.ai.tactical;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class TacticalTargetSelector {
    private final TacticalTargetScorer scorer;

    public TacticalTargetSelector() {
        this(new TacticalTargetScorer());
    }

    public TacticalTargetSelector(TacticalTargetScorer scorer) {
        this.scorer = Objects.requireNonNull(scorer, "scorer");
    }

    public Optional<TacticalTargetScore> select(
        List<TacticalTargetProfile> targets,
        TargetReservationTable reservations,
        TargetSelectionPolicy policy
    ) {
        Objects.requireNonNull(targets, "targets");
        Objects.requireNonNull(reservations, "reservations");
        Objects.requireNonNull(policy, "policy");

        return targets.stream()
            .map(scorer::score)
            .filter(score -> {
                if (!policy.avoidOverkill()) {
                    return true;
                }

                TacticalTargetProfile profile = targets.stream()
                    .filter(target -> target.targetId().equals(score.targetId()))
                    .findFirst()
                    .orElseThrow();

                int estimatedDurability = (int) Math.ceil(
                    profile.hullPercentage() * 100.0
                        + profile.shieldPercentage() * 100.0
                );

                return reservations.reservedDamage(score.targetId())
                    < estimatedDurability + policy.overkillTolerance();
            })
            .sorted()
            .findFirst();
    }
}
