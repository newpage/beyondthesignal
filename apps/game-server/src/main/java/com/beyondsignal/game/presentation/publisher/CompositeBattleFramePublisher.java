package com.beyondsignal.game.presentation.publisher;

import com.beyondsignal.game.presentation.frame.BattleFrameV1;
import java.util.List;

public final class CompositeBattleFramePublisher
    implements BattleFramePublisher {

    private final List<BattleFramePublisher> publishers;

    public CompositeBattleFramePublisher(
        List<BattleFramePublisher> publishers
    ) {
        this.publishers = List.copyOf(publishers);
    }

    @Override
    public void publish(BattleFrameV1 frame) {
        publishers.forEach(publisher -> publisher.publish(frame));
    }
}
