package com.beyondsignal.game.presentation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.beyondsignal.game.presentation.frame.*;
import com.beyondsignal.game.presentation.recorder.InMemoryFrameRecorder;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.Test;

class InMemoryFrameRecorderTest {
    @Test
    void retainsOnlyConfiguredCapacity() {
        InMemoryFrameRecorder recorder = new InMemoryFrameRecorder(2);
        recorder.record(frame(1));
        recorder.record(frame(2));
        recorder.record(frame(3));
        assertEquals(List.of(2L, 3L),
            recorder.frames().stream().map(f -> f.metadata().tick()).toList());
    }

    private static BattleFrameV1 frame(long tick) {
        return new BattleFrameV1(
            new BattleFrameMetadata(UUID.randomUUID(), tick, tick * .05, 1,
                BattleFrameV1.VERSION, Instant.EPOCH, ""),
            BattleCapabilities.core(), List.of(), List.of(), List.of(), List.of(), Map.of()
        );
    }
}
