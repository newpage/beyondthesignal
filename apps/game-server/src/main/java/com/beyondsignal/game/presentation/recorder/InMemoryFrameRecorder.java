package com.beyondsignal.game.presentation.recorder;

import com.beyondsignal.game.presentation.frame.BattleFrameV1;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Objects;

public final class InMemoryFrameRecorder implements FrameRecorder {
    private final int capacity;
    private final ArrayDeque<BattleFrameV1> frames;

    public InMemoryFrameRecorder(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be positive");
        this.capacity = capacity;
        this.frames = new ArrayDeque<>(capacity);
    }

    @Override
    public synchronized void record(BattleFrameV1 frame) {
        Objects.requireNonNull(frame, "frame");
        if (frames.size() == capacity) frames.removeFirst();
        frames.addLast(frame);
    }

    public synchronized List<BattleFrameV1> frames() {
        return List.copyOf(frames);
    }

    @Override public void flush() {}
    @Override public synchronized void clear() { frames.clear(); }
    @Override public synchronized void close() { frames.clear(); }
}
