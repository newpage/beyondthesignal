package com.beyondsignal.game.combat.event;

@FunctionalInterface
public interface CombatEventListener {
    void onEvents(CombatEventBatch batch);
}
