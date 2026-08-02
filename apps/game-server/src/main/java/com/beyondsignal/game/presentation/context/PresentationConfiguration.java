package com.beyondsignal.game.presentation.context;

import com.beyondsignal.game.presentation.frame.BattleCapabilities;
import java.util.Objects;

public record PresentationConfiguration(
    double secondsPerTick,
    BattleCapabilities capabilities
) {
    public PresentationConfiguration {
        if (!Double.isFinite(secondsPerTick) || secondsPerTick <= 0.0) {
            throw new IllegalArgumentException("secondsPerTick must be positive");
        }
        capabilities = Objects.requireNonNull(capabilities, "capabilities");
    }

    public static PresentationConfiguration defaults() {
        return new PresentationConfiguration(
            0.05,
            BattleCapabilities.core()
        );
    }
}
