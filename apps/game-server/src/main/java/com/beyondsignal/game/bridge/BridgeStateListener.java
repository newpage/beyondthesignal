package com.beyondsignal.game.bridge;

@FunctionalInterface
public interface BridgeStateListener {
    void onBridgeStateChanged(BridgeState state);
}
