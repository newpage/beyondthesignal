package com.beyondsignal.game.combat.ai.maneuver;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class ManeuverPlanner {
    private final ManeuverScorer scorer;

    public ManeuverPlanner() {
        this(new ManeuverScorer());
    }

    public ManeuverPlanner(ManeuverScorer scorer) {
        this.scorer = Objects.requireNonNull(scorer, "scorer");
    }

    public ManeuverDecision plan(
        ManeuverContext context,
        ManeuverSelectionPolicy policy
    ) {
        List<ManeuverScore> ranked = Arrays.stream(ManeuverType.values())
            .map(type -> scorer.score(type, context, policy))
            .filter(score -> score.total() > -1000)
            .sorted()
            .toList();

        if (ranked.isEmpty()) {
            throw new IllegalStateException("No maneuver is allowed by current constraints");
        }

        ManeuverScore first = ranked.getFirst();
        int secondScore = ranked.size() > 1
            ? ranked.get(1).total()
            : first.total();

        int confidence = Math.max(
            0,
            Math.min(100, 50 + Math.max(0, first.total() - secondScore))
        );

        return new ManeuverDecision(
            first.type(),
            context.objective(),
            ranked,
            confidence
        );
    }
}
