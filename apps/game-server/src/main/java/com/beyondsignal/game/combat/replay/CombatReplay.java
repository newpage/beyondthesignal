package com.beyondsignal.game.combat.replay;

import com.beyondsignal.game.combat.model.CombatId;
import java.util.List;
import java.util.Objects;

public record CombatReplay(
    CombatId combatId,
    long seed,
    List<CombatReplayFrame> frames,
    String checksum
) {
    public CombatReplay {
        combatId = Objects.requireNonNull(combatId, "combatId");
        frames = List.copyOf(Objects.requireNonNull(frames, "frames"));
        checksum = Objects.requireNonNull(checksum, "checksum");
    }

    public long totalEvents() {
        return frames.stream().mapToLong(frame -> frame.events().size()).sum();
    }
}
