package com.beyondsignal.game.presentation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.beyondsignal.game.presentation.buffer.BattleFrameBuffer;
import com.beyondsignal.game.presentation.frame.BattleCapabilities;
import com.beyondsignal.game.presentation.frame.BattleFrameMetadata;
import com.beyondsignal.game.presentation.frame.BattleFrameV1;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BattleFrameBufferTest {
    @Test
    void wrapsAndSupportsTickQueries() {
        BattleFrameBuffer buffer = new BattleFrameBuffer(3);
        for (long tick = 1; tick <= 5; tick++) {
            buffer.append(frame(tick));
        }

        assertEquals(List.of(3L, 4L, 5L), buffer.frames().stream()
            .map(value -> value.metadata().tick())
            .toList());
        assertEquals(5L, buffer.latest().orElseThrow().metadata().tick());
        assertTrue(buffer.frame(2).isEmpty());
        assertEquals(2, buffer.framesBetween(3, 4).size());
    }

    private static BattleFrameV1 frame(long tick) {
        return new BattleFrameV1(
            new BattleFrameMetadata(
                UUID.randomUUID(),
                tick,
                tick * 0.05,
                1,
                BattleFrameV1.VERSION,
                Instant.EPOCH,
                "checksum"
            ),
            BattleCapabilities.core(),
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            Map.of()
        );
    }
}
