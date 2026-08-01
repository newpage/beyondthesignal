package com.beyondsignal.game.combat.rng;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class SplitMix64CombatRandomTest {
    @Test
    void sameSeedProducesSameSequence() {
        CombatRandom first = new SplitMix64CombatRandom(42L);
        CombatRandom second = new SplitMix64CombatRandom(42L);
        for (int i = 0; i < 100; i++) assertEquals(first.nextLong(), second.nextLong());
    }

    @Test
    void stateCanBeRestored() {
        SplitMix64CombatRandom original = new SplitMix64CombatRandom(77L);
        original.nextLong();
        original.nextLong();
        SplitMix64CombatRandom restored = SplitMix64CombatRandom.restore(original.state());
        assertEquals(original.nextLong(), restored.nextLong());
        assertEquals(original.nextDouble(), restored.nextDouble());
    }

    @Test
    void differentSeedsDiverge() {
        assertNotEquals(
            new SplitMix64CombatRandom(1L).nextLong(),
            new SplitMix64CombatRandom(2L).nextLong()
        );
    }

    @Test
    void validatesInputs() {
        CombatRandom random = new SplitMix64CombatRandom(5L);
        assertThrows(IllegalArgumentException.class, () -> random.nextInt(0));
        assertThrows(IllegalArgumentException.class, () -> random.chance(-0.1));
        assertThrows(IllegalArgumentException.class, () -> random.chance(1.1));
    }
}
