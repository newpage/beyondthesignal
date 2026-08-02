package com.beyondsignal.game.presentation.events;

@FunctionalInterface
public interface BattleFrameReadyListener {
    void onFrameReady(BattleFrameReadyEvent event);
}
