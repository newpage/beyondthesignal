package com.beyondsignal.game.presentation.recorder;

import com.beyondsignal.game.presentation.frame.BattleFrameV1;

public interface FrameRecorder extends AutoCloseable {
    void record(BattleFrameV1 frame);
    void flush();
    void clear();

    @Override
    void close();
}
