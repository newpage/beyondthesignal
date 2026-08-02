package com.beyondsignal.game.combat.event;

import com.beyondsignal.game.combat.model.CombatId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Records immutable event batches for replay, analytics and diagnostics. */
public final class CombatEventRecorder implements CombatEventListener {
    private final Map<CombatId, List<CombatEventBatch>> batches =
        new LinkedHashMap<>();

    @Override
    public synchronized void onEvents(CombatEventBatch batch) {
        Objects.requireNonNull(batch, "batch");
        batches.computeIfAbsent(batch.combatId(), ignored -> new ArrayList<>())
            .add(batch);
    }

    public synchronized List<CombatEventBatch> batches(CombatId combatId) {
        return List.copyOf(batches.getOrDefault(combatId, List.of()));
    }

    public synchronized List<CombatEvent> events(CombatId combatId) {
        return batches(combatId).stream()
            .flatMap(batch -> batch.events().stream())
            .toList();
    }

    public synchronized void clear(CombatId combatId) {
        batches.remove(combatId);
    }

    public synchronized void clear() {
        batches.clear();
    }
}
