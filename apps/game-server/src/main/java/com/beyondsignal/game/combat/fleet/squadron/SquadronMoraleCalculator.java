package com.beyondsignal.game.combat.fleet.squadron;

import com.beyondsignal.game.combat.engine.CombatEncounter;
import com.beyondsignal.game.combat.engine.CombatParticipant;
import com.beyondsignal.game.combat.fleet.SquadronState;
import java.util.Objects;
import java.util.Optional;

public final class SquadronMoraleCalculator {
    public double calculate(
        CombatEncounter encounter,
        SquadronState squadron
    ) {
        Objects.requireNonNull(encounter, "encounter");
        Objects.requireNonNull(squadron, "squadron");

        if (squadron.memberIds().isEmpty()) {
            return 0.0;
        }

        double operational = squadron.memberIds().stream()
            .map(encounter::participant)
            .flatMap(Optional::stream)
            .filter(CombatParticipant::operational)
            .count() / (double) squadron.memberIds().size();

        double hull = squadron.memberIds().stream()
            .map(encounter::participant)
            .flatMap(Optional::stream)
            .filter(CombatParticipant::operational)
            .mapToDouble(participant ->
                participant.hull() / (double) participant.maximumHull()
            )
            .average()
            .orElse(0.0);

        double leader = encounter.participant(squadron.leaderId())
            .filter(CombatParticipant::operational)
            .isPresent() ? 1.0 : 0.4;

        return clamp(
            squadron.morale() * 0.35
                + operational * 0.30
                + hull * 0.20
                + leader * 0.15
        );
    }

    private static double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}
