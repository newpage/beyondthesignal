package com.beyondsignal.game.presentation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.beyondsignal.game.presentation.frame.*;
import com.beyondsignal.game.presentation.serialization.BattleFrameSerializer;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.Test;

class BattleFrameSerializerTest {
    @Test
    void roundTripsFrame() {
        BattleFrameV1 frame = new BattleFrameV1(
            new BattleFrameMetadata(UUID.randomUUID(), 3, 0.15, 4,
                BattleFrameV1.VERSION, Instant.EPOCH, "abc"),
            BattleCapabilities.core(), List.of(), List.of(), List.of(), List.of(),
            Map.of("source", "test")
        );
        BattleFrameSerializer serializer = new BattleFrameSerializer();
        assertEquals(frame, serializer.fromJson(serializer.toJson(frame)));
    }
}
