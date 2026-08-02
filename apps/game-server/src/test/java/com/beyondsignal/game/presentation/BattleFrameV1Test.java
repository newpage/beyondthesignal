package com.beyondsignal.game.presentation;

import static org.junit.jupiter.api.Assertions.*;
import com.beyondsignal.game.presentation.frame.*;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.Test;

class BattleFrameV1Test {
    @Test
    void copiesCollectionsDefensively() {
        List<BattleShipView> ships = new ArrayList<>();
        BattleFrameV1 frame = frame(ships);
        ships.add(ship());
        assertTrue(frame.ships().isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> frame.ships().add(ship()));
    }

    private static BattleFrameV1 frame(List<BattleShipView> ships) {
        return new BattleFrameV1(
            new BattleFrameMetadata(UUID.randomUUID(), 1, 0.05, 7,
                BattleFrameV1.VERSION, Instant.EPOCH, ""),
            BattleCapabilities.core(), ships, List.of(), List.of(), List.of(), Map.of()
        );
    }

    private static BattleShipView ship() {
        return new BattleShipView(UUID.randomUUID(), "Horizon", "CRUISER", "ALLIANCE",
            new PresentationVector(0,0,0), new PresentationVector(1,0,0),
            0, null, 1, 1, "ORBIT", "TURNING", 90, Set.of("SELECTABLE"));
    }
}
