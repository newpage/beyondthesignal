package com.beyondsignal.game.presentation.publisher;

import com.beyondsignal.game.presentation.frame.BattleFrameV1;

@FunctionalInterface
public interface BattleFramePublisher {
    void publish(BattleFrameV1 frame);
}
