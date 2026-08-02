package com.beyondsignal.game.presentation.events;

import com.beyondsignal.game.presentation.frame.BattleFrameV1;
import java.util.Objects;

public record BattleFrameReadyEvent(BattleFrameV1 frame) {
    public BattleFrameReadyEvent {
        frame = Objects.requireNonNull(frame, "frame");
    }
}
