package com.beyondsignal.game.presentation.buffer;

import com.beyondsignal.game.presentation.frame.BattleFrameV1;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;

public final class BattleFrameBuffer {
    private final int capacity;
    private final ArrayDeque<BattleFrameV1> frames;

    public BattleFrameBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        this.capacity = capacity;
        this.frames = new ArrayDeque<>(capacity);
    }

    public synchronized void append(BattleFrameV1 frame) {
        if (frame == null) {
            throw new IllegalArgumentException("frame is required");
        }
        if (frames.size() == capacity) {
            frames.removeFirst();
        }
        frames.addLast(frame);
    }

    public synchronized Optional<BattleFrameV1> latest() {
        return Optional.ofNullable(frames.peekLast());
    }

    public synchronized Optional<BattleFrameV1> frame(long tick) {
        return frames.stream()
            .filter(value -> value.metadata().tick() == tick)
            .findFirst();
    }

    public synchronized List<BattleFrameV1> framesBetween(
        long firstTick,
        long lastTick
    ) {
        if (firstTick < 0 || lastTick < firstTick) {
            throw new IllegalArgumentException("invalid tick range");
        }
        return frames.stream()
            .filter(frame ->
                frame.metadata().tick() >= firstTick
                    && frame.metadata().tick() <= lastTick)
            .toList();
    }

    public synchronized List<BattleFrameV1> frames() {
        return List.copyOf(frames);
    }

    public synchronized void clear() {
        frames.clear();
    }

    public int capacity() {
        return capacity;
    }
}
