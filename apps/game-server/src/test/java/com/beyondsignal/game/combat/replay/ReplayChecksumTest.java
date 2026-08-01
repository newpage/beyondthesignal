package com.beyondsignal.game.combat.replay;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import com.beyondsignal.game.combat.event.CombatEvent;
import com.beyondsignal.game.combat.event.CombatEventType;
import com.beyondsignal.game.combat.model.CombatId;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ReplayChecksumTest {
    @Test
    void sameFramesProduceSameChecksum() {
        ReplayChecksum checksum = new ReplayChecksum();
        List<CombatReplayFrame> frames = List.of(
            new CombatReplayFrame(1L, List.of(event(1L)))
        );

        assertEquals(checksum.calculate(frames), checksum.calculate(frames));
    }

    @Test
    void eventChangeChangesChecksum() {
        ReplayChecksum checksum = new ReplayChecksum();

        assertNotEquals(
            checksum.calculate(List.of(new CombatReplayFrame(1L, List.of(event(1L))))),
            checksum.calculate(List.of(new CombatReplayFrame(1L, List.of(event(2L)))))
        );
    }

    private static CombatEvent event(long sequence) {
        return new CombatEvent(
            CombatId.fromString("00000000-0000-0000-0000-000000000111"),
            sequence,
            1L,
            UUID.fromString("00000000-0000-0000-0000-000000000211"),
            UUID.fromString("00000000-0000-0000-0000-000000000212"),
            CombatEventType.WEAPON_FIRED,
            Instant.EPOCH,
            Map.of("weaponId", "phaser")
        );
    }
}
