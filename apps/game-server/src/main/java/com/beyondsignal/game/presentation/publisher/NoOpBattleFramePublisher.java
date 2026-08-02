package com.beyondsignal.game.presentation.publisher;

import com.beyondsignal.game.presentation.frame.BattleFrameV1;

public final class NoOpBattleFramePublisher implements BattleFramePublisher {
    @Override
    public void publish(BattleFrameV1 frame) {
        // Intentionally empty.
    }
}
