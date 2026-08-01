package com.beyondsignal.game.combat.ai.evaluation;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record ThreatTable(UUID observerId, List<ThreatScore> threats) {
    public ThreatTable {
        observerId = Objects.requireNonNull(observerId, "observerId");
        threats = threats.stream().sorted().toList();
    }

    public Optional<ThreatScore> highestThreat() {
        return threats.stream().findFirst();
    }
}
