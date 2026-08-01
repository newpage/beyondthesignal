package com.beyondsignal.game.combat.ai.maneuver.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class RangeKeeperTest {
    @Test
    void classifiesRangeBandsAndCommandsSpeed() {
        RangeKeeper keeper = new RangeKeeper();

        RangeControlDecision close = keeper.control(
            300.0,
            500.0,
            25.0,
            100.0
        );
        RangeControlDecision stable = keeper.control(
            510.0,
            500.0,
            25.0,
            100.0
        );
        RangeControlDecision far = keeper.control(
            800.0,
            500.0,
            25.0,
            100.0
        );

        assertEquals(RangeControlState.TOO_CLOSE, close.state());
        assertTrue(close.speedCommand() < 0.0);
        assertEquals(RangeControlState.IN_BAND, stable.state());
        assertEquals(0.0, stable.speedCommand());
        assertEquals(RangeControlState.TOO_FAR, far.state());
        assertTrue(far.speedCommand() > 0.0);
    }
}
