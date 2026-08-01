package com.beyondsignal.game.combat.ai.tactical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TargetReservationTableTest {
    @Test
    void replacesReservationForSameAttacker() {
        TargetReservationTable table = new TargetReservationTable();
        UUID attacker = UUID.randomUUID();
        UUID firstTarget = UUID.randomUUID();
        UUID secondTarget = UUID.randomUUID();

        table.reserve(new TargetReservation(attacker, firstTarget, 20));
        table.reserve(new TargetReservation(attacker, secondTarget, 30));

        assertEquals(0, table.reservedDamage(firstTarget));
        assertEquals(30, table.reservedDamage(secondTarget));
        assertTrue(table.attackerReserved(attacker));
    }
}
