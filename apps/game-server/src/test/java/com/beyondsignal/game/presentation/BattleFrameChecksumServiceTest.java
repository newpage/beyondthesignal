package com.beyondsignal.game.presentation;

import static org.junit.jupiter.api.Assertions.*;
import com.beyondsignal.game.presentation.checksum.BattleFrameChecksumService;
import com.beyondsignal.game.presentation.frame.*;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.Test;

class BattleFrameChecksumServiceTest {
    @Test
    void producesStableChecksumIgnoringChecksumField() {
        BattleFrameChecksumService service = new BattleFrameChecksumService();
        BattleFrameV1 first = frame("");
        BattleFrameV1 second = frame("already-signed");
        assertEquals(service.checksum(first), service.checksum(second));
        assertEquals(64, service.checksum(first).length());
    }

    private static BattleFrameV1 frame(String checksum) {
        UUID battleId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        return new BattleFrameV1(
            new BattleFrameMetadata(battleId, 10, 0.5, 99,
                BattleFrameV1.VERSION, Instant.EPOCH, checksum),
            BattleCapabilities.core(), List.of(), List.of(), List.of(), List.of(),
            Map.of("mode", "test")
        );
    }
}
