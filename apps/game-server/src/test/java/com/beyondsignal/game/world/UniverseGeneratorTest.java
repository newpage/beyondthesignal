package com.beyondsignal.game.world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.beyondsignal.game.galaxy.Galaxy;
import org.junit.jupiter.api.Test;

class UniverseGeneratorTest {
    private final UniverseGenerator generator = new UniverseGenerator();

    @Test
    void generatesRequestedGalaxySize() {
        Galaxy galaxy = generator.generate(42L, 3, 5);
        assertEquals(3, galaxy.sectors().size());
        assertEquals(15, galaxy.systemCount());
        assertTrue(galaxy.systems().allMatch(system -> !system.planets().isEmpty()));
    }

    @Test
    void sameSeedProducesSameGalaxy() {
        assertEquals(generator.generate(1234L, 2, 4), generator.generate(1234L, 2, 4));
    }

    @Test
    void differentSeedsProduceDifferentGalaxies() {
        assertNotEquals(generator.generate(1L, 2, 4), generator.generate(2L, 2, 4));
    }
}
