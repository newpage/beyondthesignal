package com.beyondsignal.game.presentation.runtime;

import com.beyondsignal.game.combat.engine.CombatParticipant;
import com.beyondsignal.game.combat.engine.CombatSimulationEngine;
import com.beyondsignal.game.combat.model.CombatId;
import com.beyondsignal.game.combat.model.CombatSide;
import com.beyondsignal.game.combat.shield.ShieldModel;
import io.vertx.core.Vertx;
import java.util.List;
import java.util.UUID;

public final class DeveloperBattleRuntime implements AutoCloseable {
    private final Vertx vertx;
    private final CombatSimulationEngine engine;
    private final CombatId combatId;
    private long timerId = -1;

    public DeveloperBattleRuntime(
        Vertx vertx,
        CombatSimulationEngine engine,
        long seed
    ) {
        this.vertx = vertx;
        this.engine = engine;
        this.combatId = new CombatId(UUID.nameUUIDFromBytes(
            ("developer-battle-" + seed).getBytes()
        ));
        var encounter = engine.createEncounter(combatId, seed);
        encounter.addParticipant(participant("alliance-command", CombatSide.FRIENDLY));
        encounter.addParticipant(participant("alliance-escort-1", CombatSide.FRIENDLY));
        encounter.addParticipant(participant("alliance-escort-2", CombatSide.FRIENDLY));
        encounter.addParticipant(participant("hostile-command", CombatSide.HOSTILE));
        encounter.addParticipant(participant("hostile-raider", CombatSide.HOSTILE));
        encounter.start();
    }

    public void start(long periodMillis) {
        if (periodMillis <= 0) {
            throw new IllegalArgumentException("periodMillis must be positive");
        }
        if (timerId != -1) {
            return;
        }
        timerId = vertx.setPeriodic(periodMillis, ignored -> tick());
        tick();
    }

    private void tick() {
        if (engine.encounter(combatId).isEmpty()) {
            close();
            return;
        }
        engine.tick(combatId);
    }

    private static CombatParticipant participant(
        String stableName,
        CombatSide side
    ) {
        return new CombatParticipant(
            UUID.nameUUIDFromBytes(stableName.getBytes()),
            side,
            ShieldModel.uniform(100, 1),
            List.of(),
            100,
            0.8,
            0.2
        );
    }

    @Override
    public void close() {
        if (timerId != -1) {
            vertx.cancelTimer(timerId);
            timerId = -1;
        }
    }
}
