package com.beyondsignal.game.combat.replay;

import com.beyondsignal.game.combat.engine.CombatEncounter;
import com.beyondsignal.game.combat.engine.CombatTickResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class CombatRecorder {
    private final List<CombatReplayFrame> frames = new ArrayList<>();
    private final ReplayChecksum checksum = new ReplayChecksum();

    public synchronized void record(CombatTickResult result) {
        Objects.requireNonNull(result, "result");
        frames.add(new CombatReplayFrame(result.tick(), result.eventsProduced()));
    }

    public synchronized CombatReplay finish(CombatEncounter encounter) {
        Objects.requireNonNull(encounter, "encounter");
        List<CombatReplayFrame> snapshot = List.copyOf(frames);
        return new CombatReplay(
            encounter.combatId(),
            encounter.context().seed(),
            snapshot,
            checksum.calculate(snapshot)
        );
    }

    public synchronized List<CombatReplayFrame> frames() {
        return List.copyOf(frames);
    }
}
