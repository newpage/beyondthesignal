package com.beyondsignal.game.combat.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CombatIdTest {
    @Test
    void roundTripsStringRepresentation() {
        CombatId id = new CombatId(UUID.randomUUID());
        assertEquals(id, CombatId.fromString(id.toString()));
    }

    @Test
    void rejectsBlankValue() {
        assertThrows(IllegalArgumentException.class, () -> CombatId.fromString(" "));
    }
}
