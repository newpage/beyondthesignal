package com.beyondsignal.game.combat.ai.maneuver;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ManeuverConstraintsTest {
    @Test
    void blocksFormationBreakingManeuvers() {
        ManeuverConstraints constraints = new ManeuverConstraints(
            Set.of(ManeuverType.KITE),
            true,
            false,
            false
        );

        assertFalse(constraints.allows(ManeuverType.KITE));
        assertFalse(constraints.allows(ManeuverType.FLANK_LEFT));
        assertFalse(constraints.allows(ManeuverType.ORBIT));
        assertTrue(constraints.allows(ManeuverType.HOLD_DISTANCE));
    }
}
