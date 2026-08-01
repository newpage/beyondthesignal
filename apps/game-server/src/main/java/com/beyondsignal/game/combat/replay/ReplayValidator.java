package com.beyondsignal.game.combat.replay;

import java.util.Objects;

public final class ReplayValidator {
    private final ReplayChecksum checksum;

    public ReplayValidator() {
        this(new ReplayChecksum());
    }

    public ReplayValidator(ReplayChecksum checksum) {
        this.checksum = Objects.requireNonNull(checksum, "checksum");
    }

    public boolean valid(CombatReplay replay) {
        Objects.requireNonNull(replay, "replay");
        return replay.checksum().equals(checksum.calculate(replay.frames()));
    }
}
