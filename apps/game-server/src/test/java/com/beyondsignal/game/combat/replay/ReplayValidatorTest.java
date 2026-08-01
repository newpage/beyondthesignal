package com.beyondsignal.game.combat.replay;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.beyondsignal.game.combat.model.CombatId;
import java.util.List;
import org.junit.jupiter.api.Test;

class ReplayValidatorTest {
    @Test
    void validatesReplayChecksum() {
        List<CombatReplayFrame> frames = List.of(new CombatReplayFrame(1L, List.of()));
        String checksum = new ReplayChecksum().calculate(frames);
        CombatReplay replay = new CombatReplay(
            CombatId.random(),
            1L,
            frames,
            checksum
        );

        assertTrue(new ReplayValidator().valid(replay));
        assertFalse(new ReplayValidator().valid(
            new CombatReplay(replay.combatId(), replay.seed(), replay.frames(), "bad")
        ));
    }
}
