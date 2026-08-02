package com.beyondsignal.game.presentation;

import static org.junit.jupiter.api.Assertions.*;
import com.beyondsignal.game.presentation.checksum.BattleFrameChecksumService;
import com.beyondsignal.game.presentation.frame.*;
import com.beyondsignal.game.presentation.recorder.InMemoryFrameRecorder;
import com.beyondsignal.game.presentation.service.BattlePresentationService;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.Test;

class BattlePresentationServiceTest {
    @Test
    void signsAndRecordsFrame() {
        InMemoryFrameRecorder recorder = new InMemoryFrameRecorder(10);
        BattlePresentationService service = new BattlePresentationService(
            new BattleFrameChecksumService(), List.of(recorder)
        );
        BattleFrameV1 result = service.publish(new BattleFrameV1(
            new BattleFrameMetadata(UUID.randomUUID(), 1, .05, 2,
                BattleFrameV1.VERSION, Instant.EPOCH, ""),
            BattleCapabilities.core(), List.of(), List.of(), List.of(), List.of(), Map.of()
        ));
        assertFalse(result.metadata().checksum().isBlank());
        assertEquals(result, recorder.frames().getFirst());
    }
}
